import tornado.ioloop
import tornado.web
import tornado.websocket
import json
from datetime import datetime
import uuid

def now():
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")

class MainHandler(tornado.web.RequestHandler):
    def get(self):
        with open("index.html") as f:
            data = f.read()
        self.write(data)


class EchoWebSocket(tornado.websocket.WebSocketHandler):

    connect = {}

    def check_origin(self, origin):
        return True

    def open(self):
        print("WebSocket opened")

    def on_message(self, message):
        data = json.loads(message)
        if data['type'] == "set_name":
            name = data['name']
            for con in self.connect.values():
                con.write_message(json.dumps({"message": now()+" "+ name+" joined chatroot!"}))

            self.connect[name] = self
            users = list(self.connect.keys())
            users.sort()
            for con in self.connect.values():
                con.write_message(json.dumps({"userlist": users}))

        elif data['type'] == "typing":
            name = data['name']
            for con in self.connect.values():
                if con != self:
                    con.write_message({"message": now()+" " + name+" is typing!"})

        elif data['type'] == "msg_all":
            name = data['name']
            for con in self.connect.values():
                if con != self:
                    con.write_message({"message": now()+ " name: " + name + ": "+data['content']})

        elif data['type'] == "msg_one":
            name = data['name']
            target = data['target']
            for name1, con in self.connect.items():
                if name1 == target:
                    con.write_message({"message": now()+ " name: " + name + ": "+data['content']})
        else:
            self.write_message(u"You said: " + message)

    def on_close(self):
        leave_name = None
        for name, con in self.connect.items():
            if con is self:
                leave_name = name
                break
        for name, con in self.connect.items():
            if name != leave_name:
                con.write_message(json.dumps({"message": now() + " " + leave_name + " leaving chatroot!"}))

        self.connect.pop(leave_name)

        users = list(self.connect.keys())
        users.sort()
        for con in self.connect.values():
            con.write_message(json.dumps({"userlist": users}))


if __name__ == "__main__":
    application = tornado.web.Application([
        (r"/", MainHandler),
        (r"/ws", EchoWebSocket),
    ])
    application.listen(9000)
    tornado.ioloop.IOLoop.current().start()

import memcache

mc = memcache.Client(["54.188.92.205:11211","127.0.0.1:11211"], debug=True)
mo = memcache.Client(["127.0.0.1:11211"], debug=True)
mt = memcache.Client(["54.188.92.205:11211"], debug=True)

mc.set_multi({'Title':'Start','Content':'hello world'},time=120)

title = mo.get('Title')
content = mt.get('Content')
print(title)
print(content)

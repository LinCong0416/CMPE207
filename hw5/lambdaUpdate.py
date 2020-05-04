import json
import boto3

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('hwlambda')

def lambda_handler(event, context):
    table.update_item(
        Key={
            'id':'four'
        }
    )
    UpdateExpression="SET name = :val",
    ExpressionAttributeValues={ 
        ":val":'Vicky'
    }
    response = table.scan()
    return {
         'statusCode': 200,
         'headers': { 'Content-Type': 'application/json' },
         'body': json.dumps(response)
    }


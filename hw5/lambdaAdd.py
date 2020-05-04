
import json
import boto3

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('hwlambda')

def lambda_handler(event, context):
    response = table.put_item(
        Item={
            'id':'four',
            'name':'Joey'
        }
    )
    print(response)
    return {
        'statusCode': 200,
        'body': response
    }


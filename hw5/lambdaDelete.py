import json
import boto3

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('hwlambda')

def lambda_handler(event, context):
    table.delete_item(
                      Key={
                      'id':'two'
                      }
                      )
                      response = table.scan()
                      
                      return {
                          'statusCode': 200,
                              'headers': { 'Content-Type': 'application/json' },
                                  'body': json.dumps(response["Items"])
                              }

import json
import urllib.parse
import requests
import boto3
import uuid
import xml.etree.ElementTree as ET
from datetime import datetime
print('Loading function')
s3 = boto3.client('s3')
dyn = boto3.client('dynamodb')
sns = boto3.client('sns')
dyn2 = boto3.resource('dynamodb')

def lambda_handler(event, context):
    #print("Received event: " + json.dumps(event, indent=2))
    # pull the message off of the subscribed sns topic
    message = event['Records'][0]['Sns']['Message']
    print("From SNS: " + message)
    
    try:
        # first get the doc meta from dynamo
        data = json.loads(message)
        theDocKey = data['detail']['instanceid']
        print('theDocKey ', theDocKey)
        docdata = dyn.get_item(
            TableName='test-990',
                Key={
                    'uuid': {
                        'S': theDocKey
                    }
                }
        )
        
        print('docdata is ', docdata)
        tdocdata = json.dumps(docdata)
        print('tdocdata is ', tdocdata)
        tkey = docdata["Item"]["key"]["S"]
        print("tkey is ", tkey)
        
        # now grab the doc from s3
        file_content = s3.get_object(
            Bucket='test990-s3', Key=tkey)["Body"].read()
        # print(file_content)
        
        # now call a web service with the document to be filtered
        # API call - setup the body to hold the return data to be sent
        response = requests.get("http://api.openweathermap.org/data/2.5/forecast?q=Washington,us&APPID=53d6430c1eccacb54e827045d1aee3d3")
        #print(response)
        datadictionary = response.json()
        print(datadictionary)
        
        # now update the dynamo record with new meta
        # now update the state of the object in dynamo with a state based results array
        ct = datetime.now()
        date_time = ct.strftime("%m/%d/%Y, %H:%M:%S")
        table = dyn2.Table('test-990')
        resp = table.update_item(
            Key={
                'uuid': theDocKey
            },
            UpdateExpression="set #last_update=:r, #filter_results=:a",
            ExpressionAttributeNames={
                '#last_update': 'last_update',
                '#filter_results': 'filter.results'
            },
            ExpressionAttributeValues={
                ':r': date_time,
                ':a': ["Filter A", "G Score: .876", "Y Score: .654"]
            },
            ReturnValues="UPDATED_NEW"
        )

        print("UpdateItem succeeded:")
        # print(json.dumps(resp, indent=4))
        
        # publish the event for the next compliance process
        
        
    except Exception as e:
        print(e)
        print('Error getting object {} from bucket {}. Make sure they exist and your bucket is in the same region as this function.'.format(key, bucket))
        raise e

    return message

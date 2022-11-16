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

def lambda_handler(event, context):
    #print("Received event: " + json.dumps(event, indent=2))
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
        #object_key = "hT3ThWld2OUf42163b7-d874-4295-a2cc-e360beab9eb0/KDS-S3-wAAJX-1-2022-11-16-17-35-26-feb82145-3e92-3ef7-beb0-5e181b04b7e2"
        object_key = tkey
        #object_key = tdocdata["key"]
        #print('object_key is ', object_key)
        # now grab the doc from s3
        
        file_content = s3.get_object(
            Bucket='test990-s3', Key=object_key)["Body"].read()
        
        print(file_content)
        
        # now call a web service
        # API call - setup the body to hold the return data to be sent
        response = requests.get("http://api.openweathermap.org/data/2.5/forecast?q=Washington,us&APPID=53d6430c1eccacb54e827045d1aee3d3")
        #print(response)
        datadictionary = response.json()
        print(datadictionary)
        
        # now update the dynamo record with new meta
        # now update the state of the object in dynamo with a state based results array
        
        
        # publish the event for the next compliance process
        
        
    except Exception as e:
        print(e)
        print('Error getting object {} from bucket {}. Make sure they exist and your bucket is in the same region as this function.'.format(key, bucket))
        raise e

    return message

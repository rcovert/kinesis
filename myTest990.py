import json
import urllib.parse
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
    # Get the object from the event and use the content to process the submission
    bucket = event['Records'][0]['s3']['bucket']['name']
    key = urllib.parse.unquote_plus(event['Records'][0]['s3']['object']['key'], encoding='utf-8')
    try:
        # get the json document from s3
        file_content = s3.get_object(Bucket=bucket, Key=key)["Body"].read().decode('utf-8') 
        # get the xml out of the json doc on s3
        data = json.loads(file_content)
        # get the xml payload off of the s3 document
        
        #xmlString = data["xmlText"]
        jsonString = data["jsonText"]
        jsonDict = json.loads(jsonString)
        # print(jsonString)
        # now parse the json for key values to store in dynamo
        theEIN = jsonDict["Return"]["ReturnHeader"]["Filer"]["EIN"]
        print(theEIN)
        theFilerName = jsonDict["Return"]["ReturnHeader"]["Filer"]["BusinessName"]["BusinessNameLine1Txt"]
        print(theFilerName)
        theTXPeriod = jsonDict["Return"]["ReturnHeader"]["TaxPeriodEndDt"]
        print(theTXPeriod)
        ct = datetime.now()
        date_time = ct.strftime("%m/%d/%Y, %H:%M:%S")
        
        # now parse the xml for key values to store in dynamo
        #root = ET.fromstring(xmlString)
        #ein=root.find(".//{http://www.irs.gov/efile}EIN")
        #print(ein.text)
        #theEIN = ein.text
        #filer=root.find(".//{http://www.irs.gov/efile}Filer")
        #busname=filer.find(".//{http://www.irs.gov/efile}BusinessName")
        #filername=busname.find(".//{http://www.irs.gov/efile}BusinessNameLine1Txt")
        #print(filername.text)
        #theFilerName = filername.text
        #txperiod=root.find('.//{http://www.irs.gov/efile}TaxPeriodEndDt')
        #print(txperiod.text)
        #theTXPeriod = txperiod.text
        
        # now put object into dynamo 
        # first create a unique id
        dein = str(theEIN)
        theUUID = data["uuid"]
        data = dyn.put_item(
            TableName='test-990',
            Item={
                'ein': {
                 'S': dein
                },
                'filername': {
                 'S': theFilerName
                },
                'txperiod': {
                 'S': theTXPeriod
                },
                'bucket': {
                 'S': bucket
                },
                'key': {
                 'S': key
                },
                'uuid': {
                  'S' : theUUID
                },
                'last_update': {
                  'S' : date_time
                }
            }
        )
        
        # now publish event to sns for subscribers
        # first create standard event with appropriate meta data
        eventUID = str(uuid.uuid4())
        dt = datetime.now()
        ts = str(datetime.timestamp(dt))
        # create the json
        x = '{ "version": "0", '
        x += '"id": '
        x += "\"" + eventUID + "\"" + ','
        x += '"detailtype": "Initial 990 Return Receipt",'
        x += '"source": "myTest990",'
        x += '"time": '
        x += ts + ','
        x += '"detail": {'
        x += '"instanceid": '
        x += "\"" + theUUID + "\"" + ','
        x += '"state": "initial-001"'
        x += '}'
        x += '}'

        #y = json.dumps(x)
        #print(y)
        # finally - publish the event to the topic
        # the event triggers a subscription for the next action on the content
        response = sns.publish (
            TargetArn = "arn:aws:sns:us-east-1:076667109423:990Received-Step1",
            Message = x
        )

        #return data["xmlText"]
    except Exception as e:
        print(e)
        print('Error getting object {} from bucket {}. Make sure they exist and your bucket is in the same region as this function.'.format(key, bucket))
        raise e

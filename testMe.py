import json
import xml.etree.ElementTree as ET
from datetime import datetime
import uuid

data = {
    "president": {
        "name": "Zaphod Beeblebrox",
        "species": "Betelgeusian"
    },
    "bullwinkle": "the moose"
}

with open("data_file.json", "w") as write_file:
    json.dump(data, write_file)

json_string = json.dumps(data)
print(json_string)

data = json.loads(json_string)
print(data["president"]["name"])
theName = data["president"]["name"]
print(theName)

xmlData = "<Filer><EIN>043378826</EIN><BusinessName><BusinessNameLine1Txt>RALPH TALBOT PRIMARY SCHOOL PARENT COUNCIL</BusinessNameLine1Txt></BusinessName></Filer>"
x5 = "<Filer><EIN>043378826</EIN>" +\
    "<BusinessName><BusinessNameLine1Txt>RALPH TALBOT PRIMARY SCHOOL PARENT COUNCIL</BusinessNameLine1Txt></BusinessName>" +\
    "</Filer>"

root = ET.fromstring(x5)

theEIN = root.find('EIN')
print(theEIN.text)

tree = ET.parse('t7.xml')
root2 = tree.getroot()
#print(te2.tag)
#for child in root2:
 #   print(child.tag, child.attrib)


ein=root2.find(".//{http://www.irs.gov/efile}EIN")
print(ein.text)
filer=root2.find(".//{http://www.irs.gov/efile}Filer")
busname=filer.find(".//{http://www.irs.gov/efile}BusinessName")
filername=busname.find(".//{http://www.irs.gov/efile}BusinessNameLine1Txt")
print(filername.text)
txperiod=root2.find('.//{http://www.irs.gov/efile}TaxPeriodEndDt')
print(txperiod.text)

eventUID = str(uuid.uuid4())
dt = datetime.now()
ts = str(datetime.timestamp(dt))
        
        # now publish event to sns for subscribers
        # first create standard event
x = '{ "version": "0", '
x += '"id": '
x += eventUID + ','
x += '"detail-type": "Initial 990 Return Receipt",'
x += '"source": "myTest990",'
x += '"time": '
x += ts + ','
x += '"detail": {'
x += '"instance-id": " i-1234567890abcdef0",'
x += '"state": "terminated"'
x += '}'
x += '}'
        
y = json.dumps(x);
print(y)        







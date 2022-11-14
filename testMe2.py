import json

msg = """{ \"version\": \"0\", 
    \"id\": \"de7d7c09-7716-4cc7-a2cd-57e0650a5c59\",
    \"detail-type\": \"Initial 990 Return Receipt\",
    \"source\": \"myTest990\",
    \"time\": 1668368350.649783,
    \"detail\": {\"instance-id\": \"19c2c2ce-9aa3-4d2e-95a2-c6867bcc58c1\",
    \"state\": \"terminated\"}}"""

xyz = "{ \"version\": \"0\", \"id\": de7d7c09-7716-4cc7-a2cd-57e0650a5c59,\"detail-type\": \"Initial 990 Return Receipt\",\"source\": \"myTest990\",\"time\": 1668368350.649783,\"detail\": {\"instance-id\": 19c2c2ce-9aa3-4d2e-95a2-c6867bcc58c1,\"state\": \"terminated\"}}"
yuh = "\"{ \\\"version\\\": \\\"0\\\", \\\"id\\\": de7d7c09-7716-4cc7-a2cd-57e0650a5c59,\\\"detail-type\\\": \\\"Initial 990 Return Receipt\\\",\\\"source\\\": \\\"myTest990\\\",\\\"time\\\": 1668368350.649783,\\\"detail\\\": {\\\"instance-id\\\": 19c2c2ce-9aa3-4d2e-95a2-c6867bcc58c1,\\\"state\\\": \\\"terminated\\\"}}\""

final_dictionary = eval(yuh)
print("d type is: " )
print(type(final_dictionary))
n7 = eval(final_dictionary)

#td2 = eval(xyz)
theDict = eval(msg)
#print(xyz)

print(final_dictionary)
print(theDict)

# Python3 code to convert
# a string to a dictionary

# Initializing String
string = "{'A':13, 'B':14, 'C':15}"
st6 = "{ \"version\": \"0\", \"id\": \"de7d7c09-7716-4cc7-a2cd-57e0650a5c59\"}"
dt = eval(st6)
print(dt)

# eval() convert string to dictionary
Dict = eval(string)
print(Dict)
print(Dict['A'])
print(Dict['C'])

x = """{
    \"Name\": "Jennifer Smith",
    "Contact Number": 7867567898,
    "Email": "jen123@gmail.com",
    "Hobbies":["Reading", "Sketching", "Horse Riding"]
    }"""
  
# parse x:
y = json.loads(x)

#jmsg = json.loads(msg)
#print(type(jmsg))
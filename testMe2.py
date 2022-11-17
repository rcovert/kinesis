table = dynamodb.Table('Movies')

title = "The Big New Movie"
year = 2015

response = table.update_item(
    Key={
        'year': year,
        'title': title
    },
    UpdateExpression="set #info_rating=:r, #info_plot=:p, #info_actors=:a",
    ExpressionAttributeNames={
        '#info_rating': 'info.rating',
        '#info_plot': 'info.plot',
        '#info_actors': 'info.actors'
    },
    ExpressionAttributeValues={
        ':r': decimal.Decimal(5.5),
        ':p': "Everything happens all at once.",
        ':a': ["Larry", "Moe", "Curly"]
    },
    ReturnValues="UPDATED_NEW"
)

print("UpdateItem succeeded:")
print(json.dumps(response, indent=4, cls=DecimalEncoder))

var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'us-east-1'});
console.log('Loading function');
const docClient = new AWS.DynamoDB.DocumentClient();
const m8 = '';

const params = {
  TableName : 'test-990',
  /* Item properties will depend on your application concerns */
  Key: {
    uuid: m8
  }
};

async function getItem(){
  try {
    const data = await docClient.get(params).promise();
    return data;
  } catch (err) {
    return err;
  }
}


exports.handler = async (event, context) => {
    //console.log('Received event:', JSON.stringify(event, null, 2));
    const message = event.Records[0].Sns.Message;
    console.log('From SNS:', message);
    const m7 = JSON.parse(message);
    console.log('m7 is ', m7);
    const m8 = m7.detail.instanceid;
    console.log('doc id ', m8);

    
    // connect to dynamo
    
    try {
        const data = await getItem();
        console.log('calling dynamo with ', data);
        return { body: JSON.stringify(data) };
    } catch (err) {
        return { error: err };
     }
    
    //return message;
};

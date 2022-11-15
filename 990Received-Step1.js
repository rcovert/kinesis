
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({ region: 'us-east-1' });
console.log('Loading function');
const docClient = new AWS.DynamoDB.DocumentClient();
const s3 = new AWS.S3();
const m8 = '';
const bucket = '';
const key = '';

// async function to get data from dynamo
async function getItem(tid) {
    const params = {
        TableName: 'test-990',
        /* Item properties will depend on your application concerns */
        Key: {
            uuid: tid
        }
    };

    try {
        const data = await docClient.get(params).promise();
        return data;
    } catch (err) {
        return err;
    }
}

// async function to get s3 document
async function getS3Object() {
    try {
        // Get the object} from the Amazon S3 bucket. It is returned as a ReadableStream.
        const data = await s3.getObject(bucket, key).promise();
        //console.log("Raw text:\n" + data.Body.toString('ascii'));
        // Convert the ReadableStream to a string.
        return data.Body.transformToString();
    } catch (err) {
        console.log("Error", err);
    }
};

exports.handler = async (event, context) => {
    //console.log('Received event:', JSON.stringify(event, null, 2));
    const message = event.Records[0].Sns.Message;
    //console.log('From SNS:', message);
    const m7 = JSON.parse(message);
    console.log('m7 is ', m7);
    const m8 = m7.detail.instanceid;
    console.log('doc id ', m8);

    // connect to dynamo

    try {
        const data = await getItem(m8);
        console.log('data retrieved from dynamo ', data);
        bucket = data.bucket;
        key = data.key;
        console.log('the key is ', key);
        const theDoc = await getS3Object();
        console.log('the document is: ', theDoc);
    } catch (err) {
        return { error: err };
    }

    // now get the document from S3

    // now call an api

    // now update the document in dynamo

    //return message;
};

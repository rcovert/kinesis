

const message = "{ \"version\": \"0\", \"id\": \"9ffdf179-face-4157-9bf4-f106b7b412fa\",\"detailtype\": \"Initial 990 Return Receipt\",\"source\": \"myTest990\",\"time\": 1668457179.27687,\"detail\": {\"instanceid\": \"db64e726-bd11-47d5-9f4e-72ee1358f501\",\"state\": \"terminated\"}}"

const m2 = message.replace(/\\\//g, "/");

console.log(m2);

const m3 = JSON.parse(m2);

console.log(m3);

console.log(m3.source);


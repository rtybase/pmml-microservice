import json
import sys
import urllib
import urllib2

def executePost(url, jsonPayload, headers = {}):
    headers['Content-Type'] = 'application/json'
    headers['Content-Accept'] = '*/*'
    req = urllib2.Request(url, jsonPayload, headers)
    req.get_method = lambda: 'POST'
    response = urllib2.urlopen(req)
    result = response.read()
    data = json.loads(result)
    print("\tservice\t\t\t\t" + str(data['y']) + "\t\t" + str(data['probability_0']) + "\t\t" + str(data['probability_1']))

def testWith(urlLength, probabilities_0, probabilities_1):
	payload = json.dumps({ 'urlLength': urlLength})
	executePost('http://127.0.0.1:33333/v1/classifier/phishing', payload)

jsonFile = sys.argv[1]
print("Testing with data from:" + jsonFile)

data = json.load(open(jsonFile))

print("index\tsource\turl-length\tlabel\tour_predictions\tour_probabilities_0\tour_probabilities_1")
print("------\t-----\t----------\t-----\t---------------\t-------------------\t-------------------")
for k in data['URL_Length']:
	print(k + "\tfile\t" + str(data['URL_Length'][k]) + \
		"\t\t" + str(data['label'][k]) + \
		"\t" + str(data['our_predictions'][k]) + \
		"\t\t" + str(data['our_probabilities_0'][k]) + \
		"\t\t" + str(data['our_probabilities_1'][k]))
	
	testWith(data['URL_Length'][k], data['our_probabilities_0'][k], data['our_probabilities_1'][k])
	print("------\t-----\t----------\t-----\t---------------\t-------------------\t-------------------")

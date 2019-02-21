curl http://127.0.0.1:33337/healthcheck

# good call
curl -vvv -POST -H "Expect:" -H "Accept:*/*" -H "Content-Type:application/json" -H "Connection:close" -d '{"urlLength":1}' "http://127.0.0.1:33333/v1/classifier/phishing"
echo

# outside the range
curl -vvv -POST -H "Expect:" -H "Accept:*/*" -H "Content-Type:application/json" -H "Connection:close" -d '{"urlLength":2}' "http://127.0.0.1:33333/v1/classifier/phishing"
echo
import sys, os, base64, hashlib, hmac 

# Security Policy file name
file_name = 'sec_policy.json'

service = 's3'
region = 'us-east-1'
date_stamp = '20250101'

access_key = os.environ.get('AWS_ACCESS_KEY_ID')
secret_key = os.environ.get('AWS_SECRET_ACCESS_KEY')
if access_key is None or secret_key is None:
    print('No access key is available.')
    sys.exit()

def sign(key, msg):
    return hmac.new(key, msg.encode("utf-8"), hashlib.sha256).digest()

def getSignatureKey(key, date_stamp, regionName, serviceName):
    print(key, date_stamp, regionName, serviceName)
    kDate = sign(('AWS4' + key).encode('utf-8'), date_stamp)
    kRegion = sign(kDate, regionName)
    kService = sign(kRegion, serviceName)
    kSigning = sign(kService, 'aws4_request')
    return kSigning

json_file = open(file_name, "r")
sec_policy = json_file.read() % access_key
json_file.close()

sec_policy_base64 = base64.b64encode(bytes(sec_policy, 'utf-8'))


signing_key = getSignatureKey(secret_key, date_stamp, region, service)
signature = hmac.new(signing_key, sec_policy_base64, hashlib.sha256).hexdigest()

print('Security Policy: ', sec_policy)
print('Base64 of Security Policy: ', sec_policy_base64.decode('utf-8'))
print('Signature: ', signature)

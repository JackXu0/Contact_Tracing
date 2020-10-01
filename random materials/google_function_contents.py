
# Function list:
#   1. createTeleTAN
#   2. getNewlyGeneratedPks
#   3. getRegistrationKeyGUID
#   4. getRegistrationKeyTELETAN
#   5. getTAN
#   6. uploadPeriodicKeys
#   7. verifyTAN



# createTeleTAN

# Requirements: 
#   SQLAlchemy==1.3.12 
#   PyMySQL==0.9.3
#   Flask

import sqlalchemy
from hashlib import sha256
import logging
import secrets
import string

driver_name = "mysql+pymysql"
connection_name = "contact-tracing-demo-281120:us-east4:contact-tracing-demo"
db_name = "contact_tracing_demo"
db_user = "root"
db_password = "123456"
query_string = dict({"unix_socket": "/cloudsql/{}".format(connection_name)})

logger = logging.getLogger(__name__)
alphabet = string.ascii_letters + string.digits
digits = string.digits

def getRegistrationKeyTELETAN(request):
    
    db = sqlalchemy.create_engine(
      sqlalchemy.engine.url.URL(
        drivername=driver_name,
        username=db_user,
        password=db_password,
        database=db_name,
        query=query_string,
      ),
      pool_size=5,
      max_overflow=2,
      pool_timeout=30,
      pool_recycle=1800
    )
    
    
    teletan = ''.join(secrets.choice(digits) for i in range(6))
    teletan_hashed = sha256(teletan.encode('ascii')).hexdigest()
    
    try:
      connection = db.connect()
      stmt = sqlalchemy.text('insert into registration_keys (source_type, teletan_hashed) values ("TELETAN", "'+teletan_hashed+'");')
      connection.execute(stmt)

      return teletan, 200
    except Exception as e:
      return 'Error: {}'.format(str(e)), 400

# getNewlyGeneratedPks

# Requirements:
#   SQLAlchemy==1.3.12 
#   PyMySQL==0.9.3
#   Flask

import sqlalchemy
import logging
from flask import Flask, json

driver_name = "mysql+pymysql"
connection_name = "contact-tracing-demo-281120:us-east4:contact-tracing-demo"
table_name = "periodic_keys"
db_name = "contact_tracing_demo"
db_user = "root"
db_password = "123456"
query_string = dict({"unix_socket": "/cloudsql/{}".format(connection_name)})

logger = logging.getLogger(__name__)

def downloadNew(request):
    
    db = sqlalchemy.create_engine(
      sqlalchemy.engine.url.URL(
        drivername=driver_name,
        username=db_user,
        password=db_password,
        database=db_name,
        query=query_string,
      ),
      pool_size=5,
      max_overflow=2,
      pool_timeout=30,
      pool_recycle=1800
    )

    request_json = request.get_json()
    timestamp = request_json['timestamp']
    logger.info(timestamp)

    try:
      connection = db.connect()
      stmt = sqlalchemy.text('select * from '+table_name+' where (UNIX_TIMESTAMP(timestamp)*1000) >= '+str(timestamp)+';')
      ResultProxy = connection.execute(stmt)
      ResultSet = ResultProxy.fetchall()

      keys = []
      for result in ResultSet:
        logger.info(len(result))
        logger.info(result)
        content = {'key': result[11], 'rollingStartNumber': result[2], 'rollingPeriod': result[3], 'transmissionRisk': result[4]}
        keys.append(content)

      return {"temporaryExposureKeys":keys}, 200
    except Exception as e:
        return 'Error: {}'.format(str(e)), 400
    

# getRegistrationKeyGUID

# Requirements:
#   SQLAlchemy==1.3.12 
#   PyMySQL==0.9.3
#   Flask

import sqlalchemy
from hashlib import sha256
import logging
import secrets
import string

driver_name = "mysql+pymysql"
connection_name = "contact-tracing-demo-281120:us-east4:contact-tracing-demo"
db_name = "contact_tracing_demo"
db_user = "root"
db_password = "123456"
query_string = dict({"unix_socket": "/cloudsql/{}".format(connection_name)})

logger = logging.getLogger(__name__)
alphabet = string.ascii_letters + string.digits

def getRegistrationKeyGUID(request):
    
    db = sqlalchemy.create_engine(
      sqlalchemy.engine.url.URL(
        drivername=driver_name,
        username=db_user,
        password=db_password,
        database=db_name,
        query=query_string,
      ),
      pool_size=5,
      max_overflow=2,
      pool_timeout=30,
      pool_recycle=1800
    )
    
    request_json = request.get_json()
    guid = request_json['guid']
    guid_hashed = sha256(guid.encode('ascii')).hexdigest()
    
    try:
      connection = db.connect()
      stmt = sqlalchemy.text('select registration_key_hashed from registration_keys where source_type = "GUID" and guid_hashed = "'+str(guid_hashed)+'";')
      resultProxy = connection.execute(stmt)
      resultSet = resultProxy.fetchall()
      if(len(resultSet) > 0):
        logger.info(resultSet[0][0])
        # delete corresponding registration_key and tan
        registration_key_hashed = resultSet[0][0]
        stmt = sqlalchemy.text('delete from registration_keys where source_type = "GUID" and guid_hashed = "'+str(guid_hashed)+'";')
        connection.execute(stmt)
        stmt = sqlalchemy.text('delete from tans where registration_key_hashed = "'+str(registration_key_hashed)+'";')
        connection.execute(stmt)

      new_registration_key = ''.join(secrets.choice(alphabet) for i in range(32))
      new_registration_key_hashed = sha256(new_registration_key.encode('ascii')).hexdigest()

      stmt = sqlalchemy.text('insert into registration_keys (registration_key_hashed, source_type, guid_hashed) values ("'+new_registration_key_hashed+'", "GUID", "'+guid_hashed+'");')
      connection.execute(stmt)

      return new_registration_key, 200
    except Exception as e:
      return 'Error: {}'.format(str(e)), 400
    

# getRegistrationKeyTELETAN

# Requirements:
#   SQLAlchemy==1.3.12 
#   PyMySQL==0.9.3
#   Flask

import sqlalchemy
from hashlib import sha256
import logging
import secrets
import string

driver_name = "mysql+pymysql"
connection_name = "contact-tracing-demo-281120:us-east4:contact-tracing-demo"
db_name = "contact_tracing_demo"
db_user = "root"
db_password = "123456"
query_string = dict({"unix_socket": "/cloudsql/{}".format(connection_name)})

logger = logging.getLogger(__name__)
alphabet = string.ascii_letters + string.digits

def getRegistrationKeyTELETAN(request):
    
    db = sqlalchemy.create_engine(
      sqlalchemy.engine.url.URL(
        drivername=driver_name,
        username=db_user,
        password=db_password,
        database=db_name,
        query=query_string,
      ),
      pool_size=5,
      max_overflow=2,
      pool_timeout=30,
      pool_recycle=1800
    )
    
    request_json = request.get_json()
    teletan = request_json['teletan']
    teletan_hashed = sha256(teletan.encode('ascii')).hexdigest()
    
    try:
      connection = db.connect()
      # teleTAN is valid for 30 minutes
      stmt = sqlalchemy.text('select * from registration_keys where source_type = "TELETAN" and registration_key_hashed is NULL and teletan_hashed = "'+str(teletan_hashed)+'" and create_time <  DATE_SUB(current_timestamp, INTERVAL 30 minute);')
      resultProxy = connection.execute(stmt)
      resultSet = resultProxy.fetchall()
      if(len(resultSet) > 0):
        return "TeleTAN Timeout", 400
      stmt = sqlalchemy.text('select * from registration_keys where source_type = "TELETAN" and registration_key_hashed is NULL and teletan_hashed = "'+str(teletan_hashed)+'";')
      resultProxy = connection.execute(stmt)
      resultSet = resultProxy.fetchall()
      if(len(resultSet) > 0):
        logger.info(resultSet[0][0])
        # generate correspoding registration key
        registration_key = ''.join(secrets.choice(alphabet) for i in range(32))
        registration_key_hashed = sha256(registration_key.encode('ascii')).hexdigest()
        stmt = sqlalchemy.text('update registration_keys set registration_key_hashed = "'+registration_key_hashed+'", validate_time = current_timestamp where source_type = "TELETAN" and teletan_hashed = "'+str(teletan_hashed)+'";')
        connection.execute(stmt)

        return registration_key, 200

      else:
        return "TELETAN INVALID", 400
    except Exception as e:
      return 'Error: {}'.format(str(e)), 400
    


# getTAN

# Requirements:
#   SQLAlchemy==1.3.12 
#   PyMySQL==0.9.3
#   Flask


import sqlalchemy
from hashlib import sha256
import logging
import secrets
import string

driver_name = "mysql+pymysql"
connection_name = "contact-tracing-demo-281120:us-east4:contact-tracing-demo"
db_name = "contact_tracing_demo"
db_user = "root"
db_password = "123456"
query_string = dict({"unix_socket": "/cloudsql/{}".format(connection_name)})

logger = logging.getLogger(__name__)
alphabet = string.ascii_letters + string.digits

def getTan(request):
    
    db = sqlalchemy.create_engine(
      sqlalchemy.engine.url.URL(
        drivername=driver_name,
        username=db_user,
        password=db_password,
        database=db_name,
        query=query_string,
      ),
      pool_size=5,
      max_overflow=2,
      pool_timeout=30,
      pool_recycle=1800
    )
    
    request_json = request.get_json()
    registration_key = request_json['registration_key']
    registration_key_hashed = sha256(registration_key.encode('ascii')).hexdigest()
    
    try:
      connection = db.connect()
      stmt = sqlalchemy.text('select source_type from registration_keys where registration_key_hashed = "'+str(registration_key_hashed)+'";')
      resultProxy = connection.execute(stmt)
      resultSet = resultProxy.fetchall()
      if(len(resultSet) > 0):
        source_type = resultSet[0][0]
        logger.info(source_type)
        is_guid_and_tested_positive = False
        if source_type == "GUID":
          logger.info('AAAAA')
          # TODO get test result from testing center
          is_guid_and_tested_positive = True

        if is_guid_and_tested_positive or resultSet[0][0] == "TELETAN":
          # generate a new tan for registration_key
          tan = ''.join(secrets.choice(alphabet) for i in range(16))
          tan_hashed = sha256(tan.encode('ascii')).hexdigest()
          stmt = sqlalchemy.text('delete from tans where registration_key_hashed = "'+str(registration_key_hashed)+'";')
          connection.execute(stmt)

          stmt = sqlalchemy.text('insert into tans (registration_key_hashed, tan_hashed) values ("'+registration_key_hashed+'", "'+tan_hashed+'");')
          connection.execute(stmt)

          return tan, 200  
    except Exception as e:
      return 'Error: {}'.format(str(e)), 400
    

# uploadPeriodicKeys

# Requirements:
#   SQLAlchemy==1.3.12 
#   PyMySQL==0.9.3
#   Flask

import sqlalchemy
import requests 

driver_name = "mysql+pymysql"
connection_name = "contact-shield-demo:us-central1:contact-shield-database"
table_name = "periodic_keys"
db_name = "contact_shield_demo"
db_user = "root"
db_password = "123456"
query_string = dict({"unix_socket": "/cloudsql/{}".format(connection_name)})
verify_url = 'https://us-central1-contact-shield-demo.cloudfunctions.net/verifyTAN'

def upload_periodic_key(request):
    
    db = sqlalchemy.create_engine(
      sqlalchemy.engine.url.URL(
        drivername=driver_name,
        username=db_user,
        password=db_password,
        database=db_name,
        query=query_string,
      ),
      pool_size=5,
      max_overflow=2,
      pool_timeout=30,
      pool_recycle=1800
    )
    
    request_json = request.get_json()
    periodic_keys = request_json['periodic_keys']
    api_level = request_json['api_level']
    android_version = request_json['android_version']
    brand = request_json['brand']
    model = request_json['model']
    user_id = request_json['user_id']
    tan = request_json['tan']

    payload = {'tan': tan}
    r = requests.post(url = verify_url, json = payload)

    if r.text == 'SUCCESS':  
      try:
          with db.connect() as conn:
              for periodic_key in periodic_keys:
                  pk = periodic_key['pk']
                  valid_time = periodic_key['valid_time']
                  life_time = periodic_key['life_time']
                  risk_level = periodic_key['risk_level']
                  gms_key = periodic_key['gms_key']
                  stmt = sqlalchemy.text('insert into '+table_name+' (pk, valid_time, life_time, risk_level, gms_key, api_level, android_version, brand, model, user_id) values ("'+pk+'", '+str(valid_time)+', '+str(life_time)+', '+str(risk_level)+', "'+gms_key+'", '+str(api_level)+', '+android_version+', "'+brand+'", "'+model+'", "'+user_id+'");')
                  conn.execute(stmt)
                  return 'OK', 200
      except Exception as e:
          return 'Error: {}'.format(str(e)), 400
    else:
      return "Verify failed", 400

    
# verifyTAN

# Requirements:
#   SQLAlchemy==1.3.12 
#   PyMySQL==0.9.3
#   Flask

import sqlalchemy
from hashlib import sha256
import logging
import secrets
import string

driver_name = "mysql+pymysql"
connection_name = "contact-tracing-demo-281120:us-east4:contact-tracing-demo"
db_name = "contact_tracing_demo"
db_user = "root"
db_password = "123456"
query_string = dict({"unix_socket": "/cloudsql/{}".format(connection_name)})

logger = logging.getLogger(__name__)
alphabet = string.ascii_letters + string.digits

def verifyTAN(request):
    
    db = sqlalchemy.create_engine(
      sqlalchemy.engine.url.URL(
        drivername=driver_name,
        username=db_user,
        password=db_password,
        database=db_name,
        query=query_string,
      ),
      pool_size=5,
      max_overflow=2,
      pool_timeout=30,
      pool_recycle=1800
    )
    
    request_json = request.get_json()
    tan = request_json['tan']
    tan_hashed = sha256(tan.encode('ascii')).hexdigest()
    
    try:
      connection = db.connect()
      # TAN is valid for 5 minutes
      stmt = sqlalchemy.text('select * from tans where tan_hashed = "'+str(tan_hashed)+'" and create_time <  DATE_SUB(current_timestamp, INTERVAL 5 minute);')
      resultProxy = connection.execute(stmt)
      resultSet = resultProxy.fetchall()
      if(len(resultSet) > 0):
        return "TAN TIMEOUT", 400

      stmt = sqlalchemy.text('select * from tans where tan_hashed = "'+str(tan_hashed)+'" ;')
      resultProxy = connection.execute(stmt)
      resultSet = resultProxy.fetchall()
      if(len(resultSet) > 0):
        stmt = sqlalchemy.text('delete from tans where tan_hashed = "'+str(tan_hashed)+'";')
        connection.execute(stmt)
        return "SUCCESS", 200

      else:
        return "FAILED", 400
    except Exception as e:
      return 'Error: {}'.format(str(e)), 400
    



   


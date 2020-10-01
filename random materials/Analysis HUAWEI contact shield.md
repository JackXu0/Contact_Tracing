# Analysis HUAWEI contact shield:

#### Advantages of this kit

- This kit will take responsibility of Key generation.

```java
ContactShield.getContactShieldEngine(this).getPeriodicKey();
```

- This kit will take responsibility of broadcasting and scanning.

```java
ContactShield.getContactShieldEngine(this).startContactShield(pendingIntent, ContactShieldSetting.DEFAULT) 
```

- This kit will take responsibility of contact verification

```java
ContactShield.getContactShieldEngine(this).putSharedKey(periodicKeys);
```

#### Problems to be confirmed:

- This kit also has development restriction: "Only developers authorized by governments and strictly assessed by Huawei can use Contact Shield APIs to develop apps."
- Potential problems in [documents](https://developer.huawei.com/consumer/en/doc/Contact-Shield-V1/contact-preparations-0000001050738513-V1):
  - Renew frequency of keys conflict
  - DSC key or SDC key typo
  - Why there is a need to broadcast UUID (Used in BLE?)
  - No document about cryptography process.
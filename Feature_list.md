#### Workflow:

![image-20200611120049222](C:\Users\zxu\AppData\Roaming\Typora\typora-user-images\image-20200611120049222.png)

#### Function description:

| Function                   | Description                                                  |
| -------------------------- | ------------------------------------------------------------ |
| Broadcasting and scan      | Broadcast and scan identifiers (RPI) through bluetooth. Once onReceive function of Scan is activated, the application will call checkExposure and saveRPI. |
| GenerateTEK()              | cryptographic random number generator. Will store TEK locally once generated. Will be called once a day |
| GenerateRPI()              | Will be called every 15 minites. Will store RPI locally once generated. |
| saveRPI()                  | Will store a received RPI locally                            |
| selfReport()               | Report to the server when the owner of the device is tested positive of corona virus |
| synchroTEK()               | If tested positive, TEK will be sychronized with the server once generated |
| downloadNew()              | Get newly added TEKs from server.                            |
| checkExposure()            | Check if a received RPI comes from a patient                 |
| getExposureSummary()       | Return the risk level of the device owner                    |
| makeAlert()                | Make an alert to the user if he is in danger                 |
| getTotalExposureToday()    | Return the number of total exposure today                    |
| getProlongedExpusreToday() | Return the number of prolonged exposure today                |


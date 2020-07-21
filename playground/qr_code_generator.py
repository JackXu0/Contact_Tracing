import pyqrcode

guid = "NT5HCAhDbWiFiY99kqmAFN4FYvbSal5o"

qrcode = pyqrcode.create(guid)
qrcode.svg("qrcode_guid.svg", scale=8)
import pyqrcode

guid = "ibCZYAS2nJapY7MZqYBQdoLQBL1MCFXP"

qrcode = pyqrcode.create(guid)
qrcode.svg("qrcode_guid.svg", scale=8)
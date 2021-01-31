
# cipher gen
def encrypt(_msg, _k):
    ciph = ""
    for i in _msg:
        temp = ord(i) + _k
        temp = temp if temp < 127 else temp - 95
        ciph += chr(temp)
    return ciph

# decrypt cipher
def decrypt(_ciph, _k):
    msg = ""
    for i in _ciph:
        temp = ord(i) - _k
        temp = temp if temp >= 32 else temp + 95
        msg += chr(temp)
    return msg

def getMode():
    return input("encrypt: -e / decrypt: -d: / help: -h / quit: -q: \n")

def getKey():
    key  = 0
    while True:
        try:
            key = int(input("provide an integer key for encryption: \n"))
            break
        except:
            print("error >> please provide a valid integer ")
    return key

def printRes(_in, _out, _k):
    print(">> result")
    print("   input  = %s" % _in)
    print("   output = %s" % _out)
    print("   key    = %s" % _k)

while True:
    mode = getMode()
    if mode == "-e":
        msg  = input("provide a message to encrypt: \n")
        key = getKey()
        ciph = encrypt(msg, key)
        printRes(msg, ciph, key)
    elif mode == "-d":
        ciph = input("provide a valid cipher to decrypt: \n")
        key = getKey()
        msg  = decrypt(ciph, key)
        printRes(ciph, msg, key)
    elif mode == "-h":
        print("* Caesar cipher is a method of encrypting alphabetic text \n"
              "  by shifting characters by N=key positions")
    elif mode == "-q":
        break
    else:
        print(">> error: invalid command")
        continue
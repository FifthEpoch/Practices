
# cipher gen
def encrypt(_msg, _k):
    ciph = ""
    for i in _msg:
        charcode = ord(i)
        if charcode > 64 and charcode < 91:
            charcode = charcode + _k
            charcode = charcode if charcode < 91 else charcode - 26
        ciph += chr(charcode)
    return ciph

# decrypt cipher
def decrypt(_ciph, _k):
    msg = ""
    for i in _ciph:
        charcode = ord(i)
        if charcode > 64 and charcode < 91:
            charcode = ord(i) - _k
            charcode = charcode if charcode >= 65 else charcode + 26
        msg += chr(charcode)
    return msg

def getMode():
    return input("encrypt: -e / decrypt: -d: / help: -h / quit: -q: \n")

def getInput():
    usrInput = input("provide a text containing only english characters (numbers and punctuations will be ignored.):\n")
    return usrInput.upper()

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
        msg = getInput()
        key = getKey()
        ciph = encrypt(msg, key)
        printRes(msg, ciph, key)
    elif mode == "-d":
        ciph = getInput()
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
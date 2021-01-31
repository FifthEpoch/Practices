from itertools import cycle

# cipher gen
# accepted ascii range (inclusive) 32 - 126
def encrypt(msg, key):
        k_cycle = cycle(key)
        cnt = 0
        cipher = ""
        for i in k_cycle:
            if cnt >= len(msg):
                break
            temp    = (ord(msg[cnt]) + ord(i))
            temp    = temp if temp < 127 else temp - 95
            char    = chr(temp)
            cipher += char
            cnt    += 1
        return cipher

# msg gen
def decrypt(ciph, key):
    k_cycle = cycle(key)
    cnt = 0
    msg = ""
    for i in k_cycle:
        if cnt >= len(ciph):
            break
        temp = (ord(ciph[cnt]) - ord(i))
        temp = temp if temp >= 32 else temp + 95
        char = chr(temp)
        msg += char
        cnt += 1
    return msg

def getMode():
    return input("encrypt: -e / decrypt: -d: / help: -h / quit: -q: \n")

def printRes(_in, _out, _k):
    print(">> result")
    print("   input  = %s" % _in)
    print("   output = %s" % _out)
    print("   key    = %s" % _k)

while True:
    mode = getMode()
    if mode == "-e":
        msg  = input("provide a message to encrypt: \n")
        key  = input("provide a key for encryption: \n")
        ciph = encrypt(msg, key)
        printRes(msg, ciph, key)
    elif mode == "-d":
        ciph = input("provide a valid cipher to decrypt: \n")
        key  = input("provide a key for decryption: \n")
        msg  = decrypt(ciph, key)
        printRes(ciph, msg, key)
    elif mode == "-h":
        print("* Vignere cipher is a method of encrypting alphabetic text \n"
              "  by using a series of Caesar ciphers, based on the letters of a keyword")
        print("* this implementation supports ASCII character range 32 - 126 (inclusive)\n")
    elif mode == "-q":
        break
    else:
        print(">> error: invalid command")
        continue
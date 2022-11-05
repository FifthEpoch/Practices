import math
import random

PRIME_SIZE = 10


def key_gen():

    # choose p and q
    p = get_prime_candidate(PRIME_SIZE)
    q = get_prime_candidate(PRIME_SIZE)

    # get value of n and φ(n)
    n = p * q
    phi_n = (p - 1) * (q - 1)

    # Choose e such that 1 < e < φ(n) and e and φ (n) are coprime.
    e = get_prime_candidate(int(PRIME_SIZE * 0.8))
    while e >= phi_n or e <= 1:
        e = get_prime_candidate(int(PRIME_SIZE * 0.7))

    # Compute a value for d such that (d * e) ≡ 1 (mod φ(n)).
    # First we recognize that this is a modular multiplicative inverse problem.
    # This expression is the same as saying φ(n) perfectly divides (d * e) - 1;
    # thus, φ(n) % ((d * e) - 1) = 0.
    # Let k be an integer constant, we have:
    # (d * e) - 1   =   k φ(n)

    d = extended_euclidean(e, phi_n)

    # If d = -1, it means we have exited
    # extended_euclidean() due to a zero remainder.
    # We find new e and try again.
    while d == -1:
        e = get_prime_candidate(int(PRIME_SIZE * 0.8))
        while e >= phi_n or e <= 1:
            e = get_prime_candidate(int(PRIME_SIZE * 0.7))
        d = extended_euclidean(e, phi_n)

    # Public key is (e, n)
    # Private key is (d, n)
    pub_key = (e, n)
    pri_key = (d, n)

    print(f'p:                  {p}')
    print(f'q:                  {q}')
    print(f'n:                  {n}')
    print(f'phi_n:              {phi_n}')
    print(f'e:                  {e}')
    print(f'd:                  {d}')
    print(f'public key (e, n):  {pub_key}')
    print(f'private key (d, n): {pri_key}')

    return pub_key, pri_key


def is_not_equal(_a, _b1, _b2):
    if _a == _b1 or _a == _b2:
        return False
    return True


# we find d with the extended euclidean algorithm
def extended_euclidean(_e, _phi_n):
    quad_list = []
    remainder = 0
    lhs = _phi_n
    rhs = _e

    while remainder is not 1:

        # Let EQ(1): lhs = (k * rhs) + r
        # find the largest k such that (k * rhs) < lhs
        k = math.floor(lhs / rhs)
        # find remainder r such that lhs = (k * rhs) + r
        remainder = lhs % rhs

        assert (k * rhs) + remainder == lhs
        if remainder == 0:
            # Remainder is sometimes 0. When that is the case,
            # we return to key_gen() to find a new _e,
            # and then call extended_euclidean() again.
            return -1

        # this represents the equation lhs = (k * rhs) + r
        quad_list.append([lhs, k, rhs, remainder])

        # set up for next iteration
        lhs = rhs
        rhs = remainder

    # Let EQ(2): 1 = lhs - (k * rhs)
    # back substitution in EQ(2)
    # search remainder storage for rhs substitutions
    length = len(quad_list)
    equation = [[1, quad_list[length-1][0]], [-quad_list[length-1][1], quad_list[length-1][2]]]
    while is_not_equal(equation[0][1], _e, _phi_n) or is_not_equal(equation[1][1], _e, _phi_n):

        # search remainder in storage for matches
        for i in reversed(range(len(quad_list))):

            # if matched
            if quad_list[i][3] == equation[0][1] or quad_list[i][3] == equation[1][1]:

                # find out which term in equation has the match
                matched_term = 0 if quad_list[i][3] == equation[0][1] else 1
                other_term = (matched_term + 1) % 2

                # find new overall expression for the two new terms by
                # multiplying them with coefficient of the original term.
                eq_k = equation[matched_term][0]
                new_term = [[1 * eq_k, quad_list[i][0]], [-quad_list[i][1] * eq_k, quad_list[i][2]]]

                # simplify new equation by grouping similar terms
                assert new_term[0][1] == equation[other_term][1] or new_term[1][1] == equation[other_term][1]
                if new_term[0][1] == equation[other_term][1]:
                    equation[other_term][0] += new_term[0][0]
                    equation[matched_term] = new_term[1]
                else: # new_term[1][1] == equation[other_term][1]
                    equation[other_term][0] += new_term[1][0]
                    equation[matched_term] = new_term[0]
                break

    # At the end, we should end up with
    # 1 = (k1 * φ(n)) - (k2 * e)
    # d = φ(n) - k2
    phi_n_index = 0 if equation[0][1] == _phi_n else 1
    d = _phi_n + equation[(phi_n_index+1) % 2][0]

    assert (d * _e) % _phi_n == 1

    return d


def encrypt(_msg, _pub_key):
    return (_msg ** _pub_key[0]) % _pub_key[1]


def decrypt(_cipher, _pri_key):
    return (_cipher ** _pri_key[0]) % _pri_key[1]


def is_prime(_n):
    if _n % 2 == 0 or _n < 4:
        if _n == 2 or _n == 3: return True
        return False
    limit = int(math.sqrt(_n)) + 1
    for i in range(3, limit):
        if _n % i == 0: return False
    return True


def generate_mersenne_prime(_p):
    return (2 ** _p) -1


def get_rand_bits(_bit_size):
    return random.getrandbits(_bit_size)


def get_prime_candidate(_bit_size):
    candidate = get_rand_bits(_bit_size)
    while not is_prime(candidate):
        candidate = get_rand_bits(_bit_size)
    return candidate


public_key, private_key = key_gen()
message = get_rand_bits(9)
print(f'OG message:         {message}')
encrypted = encrypt(message, public_key)
print(f'encrypted:          {encrypted}')
decrypted = decrypt(encrypted, private_key)
print(f'decrypted:          {decrypted}')


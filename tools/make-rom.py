#!/Users/sime/miniforge3/bin/python

import sys, getopt

def main(argv):

    inputfile = 'a.out'
    outputfile = 'rom.bin'
    opts, args = getopt.getopt(argv,"hi:o:",["ifile=","ofile="])
    for opt, arg in opts:
        if opt == '-h':
            print ('make-rom.py -i <inputfile> -o <outputfile>')
            sys.exit()
        elif opt in ("-i", "--ifile"):
            inputfile = arg
        elif opt in ("-o", "--ofile"):
            outputfile = arg
    print ('Input file is ', inputfile)
    print ('Output file is ', outputfile)

    # this will hold the code read in from the assembler file
    code = bytearray()

    # read in the binary file output from the assembler into a byte array and 
    with open(inputfile, mode="rb") as binfile:
        byt = binfile.read(1)
        while byt:
            code+=byt
            byt = binfile.read(1)
            
    print('Reading input file: ' + inputfile)
    print('Input file contains ' + str(len(code)) + ' bytes')
    print('Writing output to:' + outputfile)

    # the rom byte array is the code read in + X number of bytes of EA (no-op)
    # instructions to make up a 32K image
    rom = code + bytearray([0xea] * (32768 - len(code)))

    # set the reset vector address to 0x8000 as the start of the code
    rom[0x7ffc] = 0x00
    rom[0x7ffd] = 0x80

    # output the file 
    with open(outputfile, "wb") as out_file:
        out_file.write(rom)
    
if __name__ == "__main__":
   main(sys.argv[1:])
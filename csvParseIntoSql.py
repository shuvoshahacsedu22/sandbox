import sys
import os

"""
This was created for a consulting company to assist loading a very large CSV file into a database, by creating SQL statements for the following actions:
1. Creating SQL table with COL_NAMES below and their corresponding splts.
2. Parsing CSV data into SQL insert statements.

"""

DB_NAME = "Panel_Tomato_Data"

COL_NAMES = [
        "HHLD_ID", "PROCPER","TRIPDATE", "MODULE", "PRI_SHP", "SEC_SHP", "OUTLET", "UPC", 
        "UNIT", "PRICE", "DEALCODE", "COUPON", "CAS_AD", "CAS_DISP", "BRD_CD", "SIZE", "SIZE_DES", 
        "SIZE_NUM", "MULTI", "FLV_CD", "STR_TYPE", "PNL_CODE", "MFR_CPN", "PROD_GRP_NUM", "SKDS_CD", 
        "_DEAL_CD", "_2H3C_CD", "_2GVV_CD", "_27BT_CD", "_1KSG_CD", "_1KPP_CD", "_1KPO_CD", 
        "_1J3D_CD", "_1IXO_CD", "_1GW2_CD", "_1B52_CD", "_1B4Z_CD", "_1B4Y_CD", "_1ADW_CD", "_19W5_CD", 
        "_14F4_CD", "_14EO_CD", "_14EN_CD", "_14EM_CD", "_14EL_CD", "_13SL_CD", "_13SK_CD", "_13SJ_CD", 
        "_13RZ_CD", "_13MF_CD", "_1395_CD", "_1394_CD", "_1393_CD", "_137Z_CD", "_137G_CD", "_137F_CD", 
        "_136Y_CD", "_01GU_CD", "_01GD_CD", "_0145_CD", "_0121_CD", "_00YH_CD", "_00RH_CD", "_00P9_CD", 
        "_00OL_CD", "_00OG_CD", "FEM_EDU", "NUM_DOG", "NUM_CAT", "FEM_EMP", "PET_OWN", "HHLD_COM", 
        "HISPSEG", "INCOME", "NLN_REG", "NLN_CTY", "HISPANIC", "MALE_EMP", "AGEP_CHD", "RACE", "SCANTRK", 
        "NPDSTATE", "NPDCNTY", "MALE_AGE", "MALE_EDU", "HH_OCC", "FEM_AGE", "HHSIZE", "RENTOWN", 
        "Fresh Focused Segment", "Grazers Segment", "Healthy Explorers Segment", "Jugglers Segment", 
        "Price Seekers Segment", "Tolerators Segment"]

varchars = {"UPC": 12}

splts = [8, 4, 6, 4, 3, 3, 4, 12, 2, 5, 3, 4, 1, 2, 6, 2, 6, 7, 3, 6, 1, 
         1, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 
         6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 
         6, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 3, 2, 3, 1, 1, 2, 1, 
         1, 1, 1, 1, 1, 1, 1, 1]

def create_master_table():
    table_create = "create table %s ( " % DB_NAME
    for name in COL_NAMES:
        if name in varchars:
            table_create += "%s varchar(%d), " % (name.replace(' ',"_"), varchars[name])
        else:
            table_create += "%s int, " % name.replace(' ',"_")
    table_create = table_create.strip(', ')
    table_create += ")\n"
    return table_create

def create_insert_command(curr):
    values = []
    for x in splts:
        tmp = curr[0:x]
        if tmp == " ": tmp = '0' # " " into 0 for dog/cat field
        values.append(tmp)
        curr = curr[x:]
    return ",".join(values)

def main(fname):
    f = open(fname, 'r')
    o = open('insert.csv', 'w')
    o.write(create_master_table())
    o.flush()
    for e in f.readlines():
        if e:
            o.write('%s\n' % create_insert_command(e))
    f.close()
    o.close()

if __name__ == "__main__":
    main(sys.argv[1])


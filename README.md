# RA
Some codes used in data processing
------
For addressTrans.java

  Use baidu map API "Geocoding v2" to transform physical address to longitude and latitude. 
 
  The physical address should be like this: ID.Province + City + District + Village + Street + Other detailed address. 
                                           
    (e.g. 2009000003.上海徐汇区上海市田林路222号3M中国有限公司)
  
  Geocoding v2 API will return 5 values: longitude, latitude, precise, confidence, level.
  
  User must apply a key to use this service.
  

package com.mjdsoftware.logbook.utils;

import lombok.NoArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@NoArgsConstructor
public class RestUtils {

    /**
     * Perform post for object with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aClass Class
     * @param aCredentials MultiValueMap
     * @return Object
     */
    public <T> T postForObject(RestTemplate aTemplate,
                               String aUrl,
                               Object anObject,
                               Class<T> aClass,
                               Map<String, ?> anAdditionalParams,
                               MultiValueMap<String, String> aCredentials) {

        HttpEntity<Object> tempEntity;
        T                  tempResult;

        tempEntity = new HttpEntity<Object>(anObject, aCredentials);
        tempResult =  aTemplate.postForObject(aUrl,
                                              tempEntity,
                                              aClass,
                                              anAdditionalParams);

        return tempResult;
    }

    /**
     * Perform post for object
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aClass Class
     * @return Object
     */
    public <T> T postForObject(RestTemplate aTemplate,
                                  String aUrl,
                                  Object anObject,
                                  Class<T> aClass) {

        HttpEntity<Object> tempEntity;
        T                  tempResult;

        tempEntity = new HttpEntity<Object>(anObject);
        tempResult =  aTemplate.postForObject(aUrl,
                                              tempEntity,
                                              aClass);

        return tempResult;

    }

    /**
     * Perform post for object with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aClass Class
     * @return Object
     */
    public <T> T postForObject(RestTemplate aTemplate,
                               String aUrl,
                               Object anObject,
                               Class<T> aClass,
                               Map<String, ?> anAdditionalParams) {

        HttpEntity<Object>   tempEntity;
        T                    tempResult;

        tempEntity = new HttpEntity<Object>(anObject);
        tempResult =  aTemplate.postForObject(aUrl,
                                              tempEntity,
                                              aClass,
                                              anAdditionalParams);

        return tempResult;

    }


    /**
     * Post for entity
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aMap HttpEntity
     * @param aClass Class
     * @return ResponseEntity
     *
     */
    public <T> ResponseEntity<T> postForEntity(RestTemplate aTemplate,
                                               String aUrl,
                                               HttpEntity<MultiValueMap<String, Object>> aMap,
                                               Class<T> aClass) {

        return aTemplate
                .postForEntity(aUrl,
                               aMap,
                               aClass);

    }

    /**
     * Put changes to anObject
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aMap HttpEntity
     *
     */
    public void put(RestTemplate aTemplate,
                    String aUrl,
                    Object anObject,
                    MultiValueMap<String, String> aMap) {

        HttpEntity<Object>   tempEntity;


        tempEntity = new HttpEntity<Object>(anObject, aMap);
        aTemplate
                .put(aUrl, tempEntity);

    }

    /**
     * Perform put for object with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param anObject Object
     * @param aReference ParameterizedTypeReference
     * @param aCredentials HttpHeaders
     * @return ResponseEntity
     */
    public <T> ResponseEntity<T> putForObjectWithReferenceType(RestTemplate aTemplate,
                                                               String aUrl,
                                                               Object anObject,
                                                               ParameterizedTypeReference aReference,
                                                               MultiValueMap<String, String> aCredentials) {


        return aTemplate.exchange(aUrl,
                                  HttpMethod.PUT,
                                  new HttpEntity<>(anObject, aCredentials),
                                  aReference);
    }

    /**
     * Perform get for object
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aClass Class
     * @return ResponseEntity
     */
    public <T> ResponseEntity<T> getForObject(RestTemplate aTemplate,
                                              String aUrl,
                                              Class<T> aClass) {


        return aTemplate.exchange(aUrl,
                                 HttpMethod.GET,
                     null,
                                 aClass);

    }

    /**
     * Perform get for object with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aClass Class
     * @param aCredentials HttpHeaders
     * @return ResponseEntity
     */
    public <T> ResponseEntity<T> getForObject(RestTemplate aTemplate,
                                              String aUrl,
                                              Class<T> aClass,
                                              MultiValueMap<String, String>  aCredentials) {

        MultiValueMap<String, String>  tempParams = new LinkedMultiValueMap<>();

        return aTemplate.exchange(aUrl,
                                 HttpMethod.GET,
                                 new HttpEntity<>(tempParams, aCredentials),
                                 aClass);
    }

    /**
     * Perform get for object with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aCallback RequestCallback
     * @param aCredentials MultiValueMap
     * @param aDataExtractor ResponseExtractor
     */
    public void getForObject(RestTemplate aTemplate,
                             String aUrl,
                             RequestCallback aCallback,
                             MultiValueMap<String, String>  aCredentials,
                             ResponseExtractor aDataExtractor) {

        MultiValueMap<String, String>  tempParams = new LinkedMultiValueMap<>();

        aTemplate.execute(aUrl,
                          HttpMethod.GET,
                          aCallback,
                          aDataExtractor,
                          new HttpEntity<>(tempParams, aCredentials));
    }


    /**
     * Perform get for object with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aReference ParameterizedTypeReference
     * @param aCredentials HttpHeaders
     * @return ResponseEntity
     */
    public <T> ResponseEntity<T> getForObjectList(RestTemplate aTemplate,
                                                  String aUrl,
                                                  ParameterizedTypeReference aReference,
                                                  MultiValueMap<String, String> aCredentials) {

        MultiValueMap<String, String>  tempParams = new LinkedMultiValueMap<>();

        return aTemplate.exchange(aUrl,
                                 HttpMethod.GET,
                                 new HttpEntity<>(tempParams, aCredentials),
                                 aReference);
    }

    /**
     * Perform get with authentication and parameters for a reference type
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aReference ParameterizedTypeReference
     * @param aHeaders MultiValueMap
     * @return ResponseEnitty
     */
    public <T> ResponseEntity<T> getForObjectReferenceType(RestTemplate aTemplate,
                                                           String aUrl,
                                                           ParameterizedTypeReference aReference,
                                                           MultiValueMap<String, String> aHeaders) {

        MultiValueMap<String, String>  tempParams = new LinkedMultiValueMap<>();

        return aTemplate.exchange(aUrl,
                                  HttpMethod.GET,
                                  new HttpEntity<>(tempParams, aHeaders),
                                  aReference);
    }
    


    /**
     * Perform post for object with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param anObject Object
     * @param aReference ParameterizedTypeReference
     * @param aCredentials HttpHeaders
     * @return ResponseEntity
     */
    public <T> ResponseEntity<T> postForObjectWithReferenceType(RestTemplate aTemplate,
                                                              String aUrl,
                                                              Object anObject,
                                                              ParameterizedTypeReference aReference,
                                                              MultiValueMap<String, String> aCredentials) {


        return aTemplate.exchange(aUrl,
                                 HttpMethod.POST,
                                 new HttpEntity<>(anObject, aCredentials),
                                 aReference);
    }

    /**
     * Perform delete for object with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param anObject Object
     * @param aReference ParameterizedTypeReference
     * @param aCredentials HttpHeaders
     * @return ResponseEntity
     */
    public <T> ResponseEntity<T> deleteForObjectWithReferenceType(RestTemplate aTemplate,
                                                                  String aUrl,
                                                                  Object anObject,
                                                                  ParameterizedTypeReference aReference,
                                                                  MultiValueMap<String, String> aCredentials) {


        return aTemplate.exchange(aUrl,
                                  HttpMethod.DELETE,
                                  new HttpEntity<>(anObject, aCredentials),
                                  aReference);
    }


    /**
     * Perform delete with authentication
     * @param aTemplate RestTemplate
     * @param aUrl String
     * @param aClass Class
     * @param aCredentials MultiValueMap
     */
    public <T> ResponseEntity<T>
    performDeleteWithAuthentication(RestTemplate aTemplate,
                                    String aUrl,
                                    Class<T> aClass,
                                    MultiValueMap<String, String> aCredentials) {

        MultiValueMap<String, String>   tempParams = new LinkedMultiValueMap<>();


        return aTemplate.exchange(aUrl,
                                  HttpMethod.DELETE,
                                  new HttpEntity<>(tempParams, aCredentials),
                                  aClass);

    }



}

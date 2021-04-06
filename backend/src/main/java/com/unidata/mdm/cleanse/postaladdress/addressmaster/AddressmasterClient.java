package com.unidata.mdm.cleanse.postaladdress.addressmaster;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.unidata.mdm.cleanse.postaladdress.addressmaster.dto.AddressList;

/**
 * REST client consumer for http://addressmaster.ru/index.html
 *
 * @author Pavel Alexeev.
 * @created 2016-01-19 12:42.
 */
@Component
public class AddressmasterClient {
    @Value("${cleansfunctions.CFFiasCheck.addressmaster.url:http://addressmaster.ru/AutoCompleteServlet}")
    private String url;

//    RestTemplate restTemplate = new RestTemplate();
    // @TODO remove HACK. Addressmaster return incorrect text/html content type!
    RestTemplate restTemplate = new RestTemplate(){{
        MappingJackson2HttpMessageConverter converter = ((MappingJackson2HttpMessageConverter)(this.getMessageConverters()
            .stream()
            .filter(mc-> mc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElseThrow(()-> new IllegalArgumentException("Can't find MappingJackson2HttpMessageConverter in registered message converters"))
        ));

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.addAll(converter.getSupportedMediaTypes());
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(new MediaType(MediaType.TEXT_HTML.getType(), MediaType.TEXT_HTML.getSubtype(), Charset.forName("UTF-8")));
        converter.setSupportedMediaTypes(mediaTypes);

        //converter.getObjectMapper().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
    }};

    public AddressList addressMatch(String address){
        // Always request in debug to collect additional fields
        return restTemplate.getForObject(url + "?term=debug:{address}", AddressList.class, address);
    }
}

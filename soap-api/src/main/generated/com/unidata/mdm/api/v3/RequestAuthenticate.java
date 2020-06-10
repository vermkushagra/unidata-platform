
package com.unidata.mdm.api.v3;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Запрос на аутентификацию пользователя.
 * Общая часть запроса должна содержать данные для аутентификации пользователя (смотри элемент 'common' из структуры 'UnidataRequestBody').
 * Есть возможность идентифицировать пользователя двумя способами. Первый всегда по имени пользователя и паролю (элемент 'credentials' общей секции).
 * Второй способ передать сессионый токен, если данная сессия всё ещё активна, то сервер 'обновит' сессию и вернёт ответ как будто были переданы правильные имя и пароль пользователя.
 * По умолчанию сервер всегда создаёт новую сессию. Если нужно только проверить имя пользователя и пароль или получить список ролей пользователя, то можно использовать режим doLogin='false'. При этом новая сессия не создаётся.
 * Ответ сервера, при успешной аутентификации, всегда содержит список ролей пользователя. Также, в случае создании новой сессии, ответ содержит сессионный токен, который может быть использован для исполнения последующих запросов.
 *             
 * 
 * <p>Java class for RequestAuthenticate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestAuthenticate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v3/}UnidataAbstractRequest"&gt;
 *       &lt;attribute name="doLogin" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestAuthenticate")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class RequestAuthenticate
    extends UnidataAbstractRequest
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlAttribute(name = "doLogin", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected boolean doLogin;

    /**
     * Gets the value of the doLogin property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public boolean isDoLogin() {
        return doLogin;
    }

    /**
     * Sets the value of the doLogin property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setDoLogin(boolean value) {
        this.doLogin = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestAuthenticate withDoLogin(boolean value) {
        setDoLogin(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}

package ch.kochse.tools.asyncapi.codegen.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import ch.kochse.tools.asyncapi.codegen.base.GeneratorContext;
import ch.kochse.tools.asyncapi.codegen.model.ClassDescriptor;
import ch.kochse.tools.asyncapi.codegen.model.ClassProperty;
import ch.kochse.tools.asyncapi.codegen.model.DescriptorType;
import ch.kochse.tools.asyncapi.codegen.model.PropType;
import ch.kochse.tools.asyncapi.codegen.model.StdType;

public class YamlParser {
    private final static Logger log = LoggerFactory.getLogger(YamlParser.class);

    private static Yaml yamlParser ;
    private List<String> keySeq;
    private List<ClassDescriptor> classModel;
    private GeneratorContext ctx;


    public YamlParser(GeneratorContext pCtx) {
        ctx = pCtx;
        keySeq = new ArrayList<>();
        classModel = new ArrayList<>();
        yamlParser =  new Yaml();
    }

    /**
     * Parsing Yaml File and importing into model
     * @return List<ClassDescriptor> list of imported model
     */
    @SuppressWarnings("unchecked")
    public List<ClassDescriptor> parse() {
        try {
            keySeq = new ArrayList<>();
            classModel = new ArrayList<>();
            InputStream is = new FileInputStream(ctx.inputFilename());
            Map<String, Object> cont = yamlParser.load(is);
            for (String key : cont.keySet()) {
                var val  = cont.get(key);
                switch (key) {
                    case "asyncapi" : {
                        log.debug("Version of {}={}\n", key, cont.get(key).toString());
                        break;
                    }
                    case "info" : {
                        handleInfo(key, ((Map<String, Object>) val));
                        break;
                    }
                    case "servers" : {
                        handleServers(key, ((Map<String, Object>) val));
                        break;
                    }
                    case "channels" : {
                        handleChannels(key, (Map<String, Object>) val);
                        break;
                    }
                    case "components" : {
                        handleComponents(key, (Map<String, Object>) val);
                        break;
                    }
                    default: {
                        log.warn("Unhandeled tag {} in parse\n", key);
                    }
                }
            }
        } catch (FileNotFoundException pEx) {
            log.error("Error parsing {} err={}", ctx.inputFilename(), pEx.getMessage());
        }
        return classModel;
    }

    /*
     * Handlers
     */

    protected void handleInfo(String pKey, Map<String, Object> pColl) {
        log.debug("handleInfo(key={} path={})\n", pKey, generatePath(keySeq));
    }

    protected void handleServers(String pKey, Map<String, Object> pColl) {
        log.debug("handleServers(key={} path={})\n", pKey, generatePath(keySeq));
    }

    protected void handleChannels(String pKey, Map<String, Object> pColl) {
        log.debug("handleChannels(key={} path={})\n", pKey, generatePath(keySeq));
    }
    /*
     *
	  ReservationReqMsg:
      messageId: ReservationReqMsg
      name: Reservation Message
      title: Greenhouse Reservation
      summary: Inquiry of a reservation for growing some fruits or vegetables
      contentType: application/json
      payload:
        $ref: '#/components/schemas/GHReservationReqDTO'
     */
    @SuppressWarnings("unchecked")
    protected void handleMessages(String pKey, Map<String, Object> pColl) {
        log.debug("handleMessages(key={} path={})\n", pKey, generatePath(keySeq));
        keySeq.add(pKey);
        for (String key : pColl.keySet()) {
            var val = pColl.get(key);
            if (val instanceof Map<?,?>) {
                handleMessage(key, (Map<String, Object>) val);
            }
        }
        keySeq.remove(keySeq.size()-1);
    }

    /*
	    OrderReqMsg:
        messageId: OrderReqMsg
        name: Order Message
        title: Greenhouse Order
        summary: Definitive order or change of a reservation to an order
        contentType: application/json
        payload:
          $ref: '#/components/schemas/GHOrderReqDTO'
     */
    @SuppressWarnings("unchecked")
    protected void handleMessage(String pKey, Map<String, Object> pColl) {
        ClassDescriptor cld = new ClassDescriptor(generatePath(keySeq).concat(".").concat(StringUtils.capitalize(pKey)), StringUtils.capitalize(pKey));
        List<ClassProperty> meta = new ArrayList<>();
        cld.meta(meta);
        cld.descriptorType(DescriptorType.MESSAGE);
        log.debug("handleMessage(key={} path={})\n", pKey, generatePath(keySeq));
        keySeq.add(pKey);
        for (String key : pColl.keySet()) {
            switch(key) {
                case "messageId":
                case "name":
                case "title":
                case "summary":
                case "contentType":  {
                    meta.add(new ClassProperty(uniquePathKey(key), key, PropType.GENERAL).value((String) pColl.get(key))); break;
                }
                case "payload": {
                    var val = pColl.get(key);
                    if (val instanceof Map<?,?>) {
                        List<ClassProperty> pl = handleProperties(key, (Map<String, Object>) val);
                        cld.fields(pl);
                    }
                    break;
                }
                default: {
                    log.warn("Unhandeled tag {} in handleMessage\n", key);
                }
            }
        }
        classModel.add(cld);
        log.debug("add message class {}\n", cld.toString());
        keySeq.remove(keySeq.size()-1);
    }

    /*
     *
	 properties:
        inquiryId:
          type: string
          description: 'uuid to correlate the reservation to one order'
        productId:
          type: string
          description: 'product identifier'
     */
    @SuppressWarnings("unchecked")
    protected List<ClassProperty> handleProperties(String pKey, Map<String, Object> pColl) {
        List<ClassProperty> retL = new ArrayList<>();
        log.debug("handleProperties(key={} path={})\n", pKey, generatePath(keySeq));
        keySeq.add(pKey);
        for (String key : pColl.keySet()) {
            var val = pColl.get(key);
            if ("properties".equals(key)) {
                Map<String, Object> col = (Map<String, Object>) pColl.get(key);
                retL.addAll(handleProperties(pKey, col));
            } else if (val instanceof Map<?,?>) {
                Optional.ofNullable(handleProperty(key, (Map<String, Object>) val)).ifPresent(retL::add);
            } else if ("$ref".equals(key)) {
                Optional.ofNullable(handleReference(pKey, (String) val)).ifPresent(retL::add);
            }
        }
        keySeq.remove(keySeq.size()-1);
        return retL;
    }


    protected ClassProperty handleReference(String pKey, String pRef) {
        ClassProperty cp = new ClassProperty(generatePath(keySeq), pKey, PropType.FIELD);
        String[] refs = pRef.split("/");
        String type = refs[refs.length-1];
        cp.fieldType(StdType.OBJECT);
        cp.refType(type);
        // depending on the path (message or schemas handle package)
        return cp;
    }


    @SuppressWarnings("unchecked")
    protected ClassProperty handleProperty(String pKey, Map<String, Object> pColl) {
        ClassProperty cp = new ClassProperty(generatePath(keySeq).concat(".").concat(StringUtils.uncapitalize(pKey)), StringUtils.uncapitalize(pKey));
        boolean itemHandled = false;
        log.debug("handleProperty(key={} path={})\n", pKey, generatePath(keySeq));
        keySeq.add(pKey);
        for (String key : pColl.keySet()) {
            switch(key) {
                case "type":  {
                    StdType t = StdType.from((String)pColl.get(key));
                    if (StdType.ARRAY == t) {
                        cp = handleArray(pKey, pColl);
                        if (cp != null) {
                            itemHandled = true;
                        }
                    } else {
                        cp.fieldType(t);
                    }
                    break;
                }
                case "format": {
                    String format = (String) pColl.get(key);
                    cp.format(format);
                    StdType st = StdType.byFormat(format);
                    if (st != StdType.UNDEFIED) {
                        cp.fieldType(st);
                    }
                    break;
                }
                case "$ref": {
                    ClassProperty ref = handleReference(pKey, (String) pColl.get(key));
                    cp.fieldType(ref.fieldType());
                    cp.refType(ref.refType());
                    break;
                }
                case "enum": {
                    var val = pColl.get(key);
                    List<String> list = null;
                    if (val instanceof List<?>) {
                        list = (List<String>) val;
                        ClassDescriptor cl = handleEnum(pKey, StdType.from((String)pColl.get("type")), generatePath(keySeq), list);
                        log.debug("add enum: {}", cl.toString());
                        cl.description((String) pColl.get("description"));
                        classModel.add(cl);
                        cp.refType(cl.name());
                    }
                    break;
                }
                case "description": cp.description((String) pColl.get(key)); break;
                default: {
                    log.warn("Unhandeled tag '{}' in handleProperty\n", key);
                    break;
                }
            }
            if (itemHandled) break;
        }
        keySeq.remove(keySeq.size()-1);
        return cp;
    }

    protected ClassProperty handleArray(String pKey, Map<String, Object> pColl) {
        ClassProperty cp = null;
        var val = pColl.get("items");
        if ((val != null) && (val instanceof Map<?,?>)) {
            Map<String, Object> col = (Map<String, Object>) val;
            for (String key : col.keySet()) {
                switch(key) {
                    case "properties": {
                        log.error("properties: as array item is not implemented in {} use $ref:", keySeq.toString());
                        break;
                    }
                    case "type": {
                        cp = new ClassProperty(generatePath(keySeq), pKey, PropType.ARRAY);
                        cp.fieldType(StdType.from((String)col.get(key)));
                        break;
                    }
                    case "$ref": {
                        ClassProperty cr = handleReference(pKey, (String) col.get(key));
                        cp = new ClassProperty(generatePath(keySeq), pKey, PropType.ARRAY);
                        cp.fieldType(cr.fieldType());
                        cp.refType(cr.refType());
                        break;
                    }
                }
            }
        } else {
            log.error("for type array items-field missing in {}", keySeq.toString());
        }
        return cp;
    }

    protected ClassDescriptor handleEnum(String pKey, StdType pType, String pPath,  List<String> pColl) {
        String enumName = StringUtils.capitalize(pKey);
        ClassDescriptor cl = new ClassDescriptor(pPath.concat(".").concat(enumName), enumName);
        cl.descriptorType(DescriptorType.ENUM);
        List<ClassProperty> cpL = new ArrayList<>();
        for (String s : pColl) {
            ClassProperty cp = new ClassProperty(s.toUpperCase(), s);
            cp.fieldType(pType);
            cpL.add(cp);
        }
        cl.fields(cpL);
        return cl;
    }

    @SuppressWarnings("unchecked")
    protected void handleComponents(String pKey, Map<String, Object> pColl) {
        log.debug("handleComponents(key={} path={})\n", pKey, generatePath(keySeq));
        keySeq.add(pKey);
        for (String key : pColl.keySet()) {
            var val = pColl.get(key);
            switch(key) {
                case "messages": {
                    handleMessages(key, (Map<String, Object>) val);
                    break;
                }
                case "schemas": {
                    handleSchemas(key, (Map<String, Object>) val);
                    break;
                }
                case "parameters": {
                    handleParameters(key, (Map<String, Object>) val);
                    break;
                }
                case "messageTraits": {
                    handleMessageTraits(key, (Map<String, Object>) val);
                }
                default: {
                    log.warn("Unhandeled tag {} in handleComponents\n", key);
                }
            }

        }
        keySeq.remove(keySeq.size()-1);
    }

    @SuppressWarnings("unchecked")
    protected void handleParameters(String pKey, Map<String, Object> pColl) {
        log.debug("handleParameters(key={} path={})\\n", pKey, generatePath(keySeq));
        keySeq.add(pKey);
//		for (String key : pColl.keySet()) {
//			var val = pColl.get(key);
//			if (val instanceof Map<?,?>) {
//				handleSchema(key, (Map<String, Object>) val);
//			} else {
//				log.warn("Unhandeled tag {} in handleSchemas\n", key);
//			}
//		}
        keySeq.remove(keySeq.size()-1);
    }

    @SuppressWarnings("unchecked")
    protected void handleMessageTraits(String pKey, Map<String, Object> pColll)  {
        log.warn("to implement handleMessageTraits(key={} path={})\\n", pKey, generatePath(keySeq));
    }

    @SuppressWarnings("unchecked")
    protected void handleSchemas(String pKey, Map<String, Object> pColl) {
        log.debug("handleSchemas(key={} path={})\\n", pKey, generatePath(keySeq));
        keySeq.add(pKey);
        for (String key : pColl.keySet()) {
            var val = pColl.get(key);
            if (val instanceof Map<?,?>) {
                handleSchema(key, (Map<String, Object>) val);
            } else {
                log.warn("Unhandeled tag {} in handleSchemas\n", key);
            }
        }
        keySeq.remove(keySeq.size()-1);
    }

    @SuppressWarnings("unchecked")
    protected void handleSchema(String pKey, Map<String, Object> pColl) {
        ClassDescriptor cld = new ClassDescriptor(generatePath(keySeq).concat(".").concat(StringUtils.capitalize(pKey)), StringUtils.capitalize(pKey));
        List<ClassProperty> meta = new ArrayList<>();
        log.debug("handleSchema(key={} path={})\n", pKey, generatePath(keySeq));
        if (pKey.endsWith("DTO")) {
            cld.descriptorType(DescriptorType.DTO);
        } else {
            cld.descriptorType(DescriptorType.CLASS);
        }

        keySeq.add(pKey);
        for (String key : pColl.keySet()) {
            switch(key) {
                case "type":  break;
                case "description":  cld.description((String)pColl.get(key)); break;
                case "name":
                case "title":
                case "summary":
                case "contentType":  {
                    meta.add(new ClassProperty(generatePath(keySeq).concat(".").concat(key), key, PropType.GENERAL).value((String) pColl.get(key))); break;
                }
                case "properties": {
                    var val = pColl.get(key);
                    if (val instanceof Map<?,?>) {
                        List<ClassProperty> pl = handleProperties(key, (Map<String, Object>) val);
                        cld.fields(pl);
                    }
                    break;
                }
                case "enum": {
                    var val = pColl.get(key);
                    List<String> list = null;
                    if (val instanceof List<?>) {
                        list = (List<String>) val;
                        ClassDescriptor cl = handleEnum(pKey, StdType.from((String)pColl.get("type")), generatePath(keySeq), list);
                        log.debug("add enum: {}", cl.toString());
                        cl.description((String) pColl.get("description"));
                        cld = cl;
                    }
                    break;
                }
                default: {
                    log.warn("Unhandeled tag {} in handleSchema\n", key);
                }
            }
        }
        cld.meta(meta);
        classModel.add(cld);
        log.debug("add schema class {}\n", cld.toString());
        keySeq.remove(keySeq.size()-1);
    }

    private String uniquePathKey(String pKey) {
        return generatePath(keySeq).concat(".").concat(pKey);
    }

    private String generatePath(List<String> pPath) {
        return StringUtils.collectionToDelimitedString(pPath, ".");
    }
}

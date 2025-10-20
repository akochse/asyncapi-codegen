package ch.kochse.lib.common.messaging.base;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageType
implements IMessageType {

    private final String name;
    private final Class<? extends IMessage<? extends IDTO>> type;
    private final Class<? extends IDTO> payloadType;

    private static final Map<String, IMessageType> messageTypes = new HashMap<>();

    public MessageType(final String pName, Class<? extends IMessage<? extends IDTO>> pType, Class<? extends IDTO> pPayload) {
        if ((pName == null) || (pName.isBlank())) {
            throw new NullPointerException("message type name may not be null or empty");
        }
        name = pName;
        type = pType;
        payloadType = pPayload;
        messageTypes.put(name, this);
    }

    public MessageType(final String pName, Class<? extends IMessage<?>> pType) {
        this(pName, pType, null);
    }


    public static IMessageType messageTypeFor(String pTypeName) {
        return messageTypes.get(pTypeName);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<? extends IMessage<? extends IDTO>> type() {
        return type;
    }

    @Override
    public Class<? extends IDTO> payloadType() {
        return payloadType;
    }

    @Override
    public Optional<IMessage<? extends IDTO>> messageFor() {
        Optional<IMessage<?>> message = Optional.empty();
        try {
            message = Optional.of((IMessage<?>) constructorFor(type()).newInstance());
        } catch (Exception pEx) {}
        return message;
    }

    @Override
    public Optional<IMessage<? extends IDTO>> messageFor(Class<? extends IDTO> pPayloadClass) {
        Optional<IMessage<? extends IDTO>> message = Optional.empty();
        if (pPayloadClass != null) {
            for (IMessageType mty : messageTypes.values()) {
                if (pPayloadClass.equals(mty.payloadType())) {
                    message = mty.messageFor();
                    break;
                }
            }
        }
        return message;
    }


    @Override
    public Optional<IDTO> payloadDtoFor() {
        Optional<IDTO> payloadDto = Optional.empty();
        if (payloadType() != null) {
            try {
                payloadDto = Optional.of((IDTO) constructorFor(payloadType()).newInstance());
            } catch (Exception pEx) {}
        }
        return payloadDto;
    }

    @Override
    public Optional<IDTO> payloadDtoFor(Class<? extends IMessage<? extends IDTO>> pMsgClass) {
        Optional<IDTO> payloadDto = Optional.empty();
        if (pMsgClass != null) {
            for (IMessageType mty : messageTypes.values()) {
                if (pMsgClass.equals(mty.type())) {
                    payloadDto = mty.payloadDtoFor();
                    break;
                }
            }
        }
        return payloadDto;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append("::").append(type.getSimpleName());
        if(payloadType != null) {
            sb.append("/").append(payloadType.getSimpleName());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object pThat) {
        if (this == pThat) {
            return true;
        }
        if (!(pThat instanceof IMessageType)) {
            return false;
        }
        return name.equals(((IMessageType) pThat).name());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private Constructor<?> constructorFor(Class<?> pClass) {
        Constructor<?>[] ctors = pClass.getDeclaredConstructors();
        Constructor<?> ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
        return ctor;
    }
}

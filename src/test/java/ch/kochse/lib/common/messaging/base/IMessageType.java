package ch.kochse.lib.common.messaging.base;

import java.util.Optional;

public interface IMessageType {
    /**
     * @return String name of type
     */
    String name();

    /**
     * @return type of the message
     */
    Class<? extends IMessage<? extends IDTO>> type();

    /**
     * @return type of the payload
     */
    Class<? extends IDTO> payloadType();

    /**
     * @return Optional<IMessage> instance of message class for type
     */
    Optional<IMessage<? extends IDTO>> messageFor();

    /**
     * @return Optional<IDTO> instance of a dto for payload
     */
    Optional<IDTO> payloadDtoFor();

    /**
     * create a message instance for the passed DTO class
     * @param pPayloadClass Class<? extends IDTO>
     * @return Optional<IMessage>
     */
    Optional<IMessage<? extends IDTO>> messageFor(Class<? extends IDTO> pPayloadClass);

    /**
     * create a DTO instance as payload for message
     * @param pMsgClass Class<? extends IMessage>
     * @return Optional<IDTO>
     */
    Optional<? extends IDTO> payloadDtoFor(Class<? extends IMessage<? extends IDTO>> pMsgClass);


    /**
     * compares msgTypes
     * @param pMsgType
     * @return boolean true if the same
     */
    default boolean equals(IMessageType pMsgType) {
        return (name().equals(pMsgType.name()));
    }

    /**
     * Instantiates a new {@linkplain MessageType} with the given name.
     *
     * @param pName the name of the message type
     * @param pType the message class assiociated with the type
     * @param pPayload the payload class assiociated with the type
     * @return a {IMessageTye} instance for the given name
     * @throws NullPointerException if pName is {@code null}
     */
    static IMessageType messageType(final String pName, Class<? extends IMessage<? extends IDTO>> pType, Class<? extends IDTO> pPayload) throws NullPointerException {
        if ((pName == null) || (pName.isBlank())) {
            throw new NullPointerException("message type name may not be null or empty");
        }
        return new MessageType(pName, pType, pPayload);
    }

    /**
     * Instantiates a new {@linkplain MessageType} with the given name.
     *
     * @param pName the name of the message type
     * @param pType the message class assiociated with the type
     * @return a {IMessageTye} instance for the given name
     * @throws NullPointerException if pName is {@code null}
     */
    static IMessageType messageType(final String pName, Class<? extends IMessage<? extends IDTO>> pType) throws NullPointerException {
        return messageType(pName, pType, null);
    }


}

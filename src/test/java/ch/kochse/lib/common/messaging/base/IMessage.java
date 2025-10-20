package ch.kochse.lib.common.messaging.base;

public interface IMessage<D extends IDTO> {
    /**
     * @return long version of the message structure
     */
    String getVersion();

    /**
     * compares the existing version of the message with the past version
     * @param pVersion String incoming version (such as from a received message)
     * @return true considered that the versions are compatible false if the messages are not compatible
     */
    default boolean verifyVersion(String pVersion) {
        String version = getVersion();
        if (version.equals(pVersion)) {
            return true;
        }
        String[] myVersion = version.split("\\.");
        String[] rcvdVersion = pVersion.split("\\.");

        return ((myVersion.length == rcvdVersion.length) &&
                (myVersion[0].equals(rcvdVersion[0])) &&
                (myVersion[1].compareTo(rcvdVersion[1]) >= 0) &&
                (myVersion[2].compareTo(rcvdVersion[2]) >= 0));
    }

    /**
     * @param pOperation operation to perform with the passed message
     * @return IMessage
     */
    IMessage setOperation(String pOperation);

    /**
     * @return String or null operation to perform with the message
     */
    String getOperation();
    /*
     * Nachfolgende Attribute werden nicht ins DTO übernommen, sondern
     * dienen intern für das weitergeben von Informationen der Meldung
     */
    /**
     * @return String message id
     */
    String correlationID();

    /**
     * set message ID
     * @param pId String
     * @return IMessage
     */
    IMessage<D> correlationID(String pId);

    /**
     * Returns the raw payload
     * @return IDTO payload
     */
    D payload();

    /**
     * Sets the payload and checks if the type is correct for this message
     * @param pPayload IDTO payload
     * @throws ClassCastException if payload does not match the payload type of the message
     */
    void payload(D pPayload);

    /**
     * @return IMessageType message type
     */
    IMessageType type();
    /**
     * @return String destination where the message should be sent to
     */
    String to();
    /**
     * @return String destination where the message was sent from
     */
    String from();
    /**
     * @param pTo String destination the message should be sent to
     * @return IMessage
     */
    IMessage<D> to(String pTo);
    /**
     * @param pFrom String destination the message was sent from
     * @return IMessage
     */
    IMessage<D> from(String pFrom);
}

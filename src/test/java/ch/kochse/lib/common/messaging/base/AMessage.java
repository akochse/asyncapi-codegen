package ch.kochse.lib.common.messaging.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AMessage<D extends IDTO> implements IMessage<D> {

    private IMessageType		type;
    private String			correlationID;
    private String			version;
    private String			operation;
    private String			from;
    private String		    to;

    public AMessage() {
        type = null;
    }

    public AMessage(IMessageType pType) {
        this();
        type = pType;
    }

    public AMessage(IMessageType pType, String pVersion) {
        this(pType);
        version = pVersion;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    @JsonIgnore
    public IMessageType type() {
        return type;
    }

    public String getType() {
        return type.name();
    }

    public IMessage<D> setType(IMessageType pType) {
        type = pType;
        return this;
    }

    public IMessage<D> setType(String pType) {
        type = MessageType.messageTypeFor(pType);
        return this;
    }

    @Override
    public IMessage<D> setOperation(String pOperation) {
        operation = pOperation;
        return this;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    @JsonIgnore
    public String correlationID() {
        return correlationID;
    }

    @Override
    @JsonIgnore
    public IMessage<D> correlationID(String pId) {
        correlationID = pId;
        return this;
    }

    @Override
    @JsonIgnore
    public String to() {
        return to;
    }

    @Override
    @JsonIgnore
    public IMessage<D> from(String pFrom) {
        from = pFrom;
        return this;
    }

    @Override
    @JsonIgnore
    public IMessage<D> to(String pTo) {
        to = pTo;
        return this;
    }

    @Override
    @JsonIgnore
    public String from() {
        return from;
    }

    @Override
    public String toString() {
        return new StringBuilder(getType())
                .append(": id:").append(correlationID())
                .append(" version:").append(getVersion())
                .append(" type:").append(getType())
                .append(" operation: ").append(getOperation())
                .append(" to:").append(to())
                .append(" from: ").append(from())
                .toString();
    }

}

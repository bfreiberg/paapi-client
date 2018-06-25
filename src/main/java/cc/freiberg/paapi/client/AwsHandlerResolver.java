package cc.freiberg.paapi.client;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class AwsHandlerResolver implements HandlerResolver {
    private final String awsSecretKey;

    public AwsHandlerResolver(final String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List<Handler> getHandlerChain(final PortInfo portInfo) {
        final List<Handler> handlerChain = new ArrayList<>();

        final QName serviceQName = portInfo.getServiceName();
        if (serviceQName.getLocalPart().equals("AWSECommerceService")) {
            handlerChain.add(new AwsHandler(awsSecretKey));
        }

        return handlerChain;
    }

    private static class AwsHandler implements SOAPHandler<SOAPMessageContext> {
        private final byte[] secretBytes;

        public AwsHandler(final String awsSecretKey) {
            secretBytes = stringToUtf8(awsSecretKey);
        }

        @Override
        public void close(final MessageContext messagecontext) {
        }

        @Override
        public Set<QName> getHeaders() {
            return null;
        }

        @Override
        public boolean handleFault(final SOAPMessageContext messagecontext) {
            return true;
        }

        @Override
        public boolean handleMessage(final SOAPMessageContext messagecontext) {
            final Boolean outbound = (Boolean) messagecontext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (outbound) {
                try {
                    final SOAPMessage soapMessage = messagecontext.getMessage();
                    final SOAPBody soapBody = soapMessage.getSOAPBody();
                    final Node firstChild = soapBody.getFirstChild();

                    final String timeStamp = getTimestamp();
                    final String signature = getSignature(firstChild.getLocalName(), timeStamp, secretBytes);

                    appendTextElement(firstChild, "Signature", signature);
                    appendTextElement(firstChild, "Timestamp", timeStamp);
                } catch (final SOAPException se) {
                    throw new RuntimeException("SOAPException was thrown.", se);
                }
            }
            return true;
        }

        private static String getSignature(final String operation, final String timeStamp, final byte[] secretBytes) {
            try {
                final String toSign = operation + timeStamp;
                final byte[] toSignBytes = stringToUtf8(toSign);

                final Mac signer = Mac.getInstance("HmacSHA256");
                final SecretKeySpec keySpec = new SecretKeySpec(secretBytes, "HmacSHA256");

                signer.init(keySpec);
                signer.update(toSignBytes);
                final byte[] signBytes = signer.doFinal();

                final String signature = new String(Base64.encodeBase64(signBytes));
                return signature;
            } catch (final NoSuchAlgorithmException nsae) {
                throw new RuntimeException("NoSuchAlgorithmException was thrown.", nsae);
            } catch (final InvalidKeyException ike) {
                throw new RuntimeException("InvalidKeyException was thrown.", ike);
            }
        }

        private static String getTimestamp() {
            final Calendar calendar = Calendar.getInstance();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat.format(calendar.getTime());
        }

        private static byte[] stringToUtf8(final String source) {
            try {
                return source.getBytes("UTF-8");
            } catch (final UnsupportedEncodingException e) {
                // This will never happen. UTF-8 is always available.
                throw new RuntimeException("getBytes threw an UnsupportedEncodingException", e);
            }
        }

        private static void appendTextElement(final Node node, final String elementName, final String elementText) {
            final Element element = node.getOwnerDocument().createElement(elementName);
            element.setTextContent(elementText);
            node.appendChild(element);
        }
    }
}

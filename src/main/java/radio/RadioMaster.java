package radio;

import generated.grpc.radio.TerminatorGrpc;
import io.grpc.Server;

public class RadioMaster {

    private Server server;

    private class Terminator extends TerminatorGrpc.TerminatorImplBase {

    }

}

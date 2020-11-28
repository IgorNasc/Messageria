package main;

interface StrProperty{
    public String UNICAST   = "unicast";
    public String BROADCAST = "broadcast";
    public String ALL       = "###";
}

interface MsgPadrao{
    public String MENSAGEM_INVALIDA = "Houve um erro.";
    public String BT_CONECTAR       = "Entrar";
    public String BT_DESCONECTAR    = "Sair";
    public String MENSAGEM_VAZIA    = "Em uma macthup de Riven contra Irelia...";
}

interface Conf{
    public String QUEUE_CHAT = "QueueChat";
    public String TOPIC_CHAT = "TopicChat";
    public String FACTORY    = "ConnectionFactory";
}
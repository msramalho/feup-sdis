https://web.fe.up.pt/~pfs/aulas/sd2017/labs/lab2.html

IPadress, Port
IPadress must be between 224.0.0.0 and 239.255.255.2525


cliente subscrevem furpo multicast
servidor envia mensagem multicast a dizer qual é porto em que o servidor está a fornecer o serviço
depois o cliente usa multicast para isso

servidor faz multicast periódico e outra é prestar o serviço
    . pode ser com threads. Classes Timer e TimerTask (java.util), para executar uma tarefa que se repete de t em t. Há também java.util.Concurrent que é mais completa.
    . ou com unico thread. mas como o receive() é bloqueante, cria-se um deadlock no servidor. contudo dá para usar .setScoketTimeout(int time) para o .receive()

Classes:
MulticastSocket . igual a unicast, com a restrição de o ip ter de estar na gama, é possível usar DatagramPacket
cliente- para a receção tem de ser com a MulticastSocket (porque têm o joinGrupo(InetAddres mcastAddr)), para especificar o porto do grupo é preciso um socket associado ao porto correspondente - usar o construtor com o porto (MulticastSocket(int port)). Quando acabar as ações deve usar leaveGroup(InetAddres mcastAddr).
quem envia pode user o MulticastSocket mesmo sem estar no grupo (multicast é aberto (não é preciso pertencer ao grupo para enviar mensagens)), contudo, o MulticastSocket tem setTimeToLive(int ttl) faz parte do protocolo IP e permite não ficar preso em ciclos E limitar o multicast (usar 1)
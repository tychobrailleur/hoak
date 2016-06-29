eb = $vertx.event_bus()

eb.consumer("news.uk.sport") { |message|
  puts "I have received a message: #{message.body()}"
}
package com.jpmc.midascore.service;

import com.jpmc.midascore.model.Transaction;
import com.jpmc.midascore.model.User;
import com.jpmc.midascore.model.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @KafkaListener(topics = "transactions", groupId = "midas")
    public void consume(String message) {
        Transaction tx = Transaction.fromString(message); // Assuming you have a parser
        Optional<User> senderOpt = userRepository.findById(tx.getSenderId());
        Optional<User> recipientOpt = userRepository.findById(tx.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) return;

        User sender = senderOpt.get();
        User recipient = recipientOpt.get();

        BigDecimal amount = tx.getAmount();
        if (sender.getBalance().compareTo(amount) >= 0) {
            sender.setBalance(sender.getBalance().subtract(amount));
            recipient.setBalance(recipient.getBalance().add(amount));

            userRepository.save(sender);
            userRepository.save(recipient);

            TransactionRecord record = new TransactionRecord(sender, recipient, amount, LocalDateTime.now());
            transactionRecordRepository.save(record);
        }
    }
}

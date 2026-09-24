package mx.taller.user; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User,Long>{ Optional<User> findByEmail(String email); Optional<User> findByResetToken(String token); }

package com.example.demo.jpas;


import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJPA extends JpaRepository<User, Integer>{
	@Query(value = "SELECT * FROM users WHERE username=?1 OR email=?2", nativeQuery = true)
	public List<User> findByUsernameAndEmail(String username, String email);

	@Query(value = "SELECT * FROM users WHERE (username=?1 OR email=?2) AND id != ?3", nativeQuery = true)
	public List<User> findByUsernameOrEmailAndId(String username, String email, int id);

	// Tìm user theo username
//    @Query(value = "SELECT * FROM users WHERE username = ?1", nativeQuery = true)
//    List<User> findByUsername(String username);
    Optional<User> findByUsername(String username);

    // Tìm user theo email
    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    List<User> findByEmail(String email);
    
    
    @Query(value = "SELECT * FROM users WHERE username=?1", nativeQuery = true)
    public Optional<User> findByUsername1(String username);
    
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername2(String username);
    
    @Query(value = "SELECT * FROM users WHERE role <> 0", nativeQuery = true)
    List<User> findAllUsers();

   // Optional<User> findFirstByEmail(String email);




    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findFirstByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = 2 AND (u.username LIKE %:search% OR u.email LIKE %:search% OR u.fullName LIKE %:search%)")
    Page<User> findByUsernameContainingOrEmailContainingOrFullNameContaining(
            @Param("search") String search,
            @Param("search") String search2,
            @Param("search") String search3,
            Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = 2 AND u.isActive = :isActive")
    Page<User> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = 2 AND (u.username LIKE %:search% OR u.email LIKE %:search% OR u.fullName LIKE %:search%) AND u.isActive = :isActive")
    Page<User> findByUsernameContainingOrEmailContainingOrFullNameContainingAndIsActive(
            @Param("search") String search,
            @Param("search") String search2,
            @Param("search") String search3,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = 2")
    Page<User> findAllByRole(Pageable pageable);
}

package ru.itmo.secureapi.user;

public record AppUser(long id, String username, String passwordHash) {
}


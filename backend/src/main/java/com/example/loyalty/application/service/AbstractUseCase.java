package com.example.loyalty.application.service;

public abstract class AbstractUseCase<I, O> { public abstract O execute(I input); }

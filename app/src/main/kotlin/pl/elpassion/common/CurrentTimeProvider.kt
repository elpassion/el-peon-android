package pl.elpassion.common

object CurrentTimeProvider : Provider<Long>({ System.currentTimeMillis() })
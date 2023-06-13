package minesweeper

import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess

const val height = 9
const val width = 9
val answerBoard = MutableList(height) {"/".repeat(width).toCharArray()}
val playerBoard = MutableList(height) {".".repeat(width).toCharArray()}
var mines = 0
var minesFound = 0
var minesFlagged = 0
var spacesRevealed = 0
var firstFreeMove = false
var playerLoses = false

fun printBoard() {
    print("\n │")
    (1..width).forEach { print(it) }
    print("│\n")
    println("—│${"—".repeat(width)}│")
    when {
        playerLoses -> {
            for (r in answerBoard.indices) {
                print("${r+1}│")
                for(c in answerBoard[r].indices) print(if (answerBoard[r][c] == 'X') "X" else playerBoard[r][c])
                print("│\n")
            }
        }
        !playerLoses -> for (i in playerBoard.indices) println("${i+1}│${playerBoard[i].joinToString("")}│")
    }
    println("—│${"—".repeat(width)}│")
    println()
}

fun setMinesAndNumbers(y: Int, x: Int) {
    var minesLaid = mines
    while (minesLaid != 0) {
        val col = Random.nextInt(width)
        val row = Random.nextInt(height)
        if (firstFreeMove && col == y && row == x) continue
        if (answerBoard[row][col] != 'X') {
            answerBoard[row][col] = 'X'
            minesLaid--
            for (r in (row - 1)..(row + 1)) {
                for (c in (col - 1)..(col + 1)) {
                    try {
                        when (answerBoard[r][c]) {
                            'X' -> continue
                            '/' -> answerBoard[r][c] = '1'
                            in '1'..'7' -> answerBoard[r][c]++
                        }
                    } catch (e: IndexOutOfBoundsException) { continue }
                }
            }
        } else continue
    }
}

fun inputMine(r: Int, c: Int) {
    when (playerBoard[r][c]) {
        '.' -> {
            playerBoard[r][c] = '*'
            minesFlagged++
        }
        '*' -> {
            playerBoard[r][c] = '.'
            minesFlagged--
        }
    }
    printBoard()
    checkWin()
}

fun inputFree(r:Int, c: Int) {
    if (!firstFreeMove) {
        firstFreeMove = true
        setMinesAndNumbers(r, c)
    }
    when (answerBoard[r][c]) {
        'X' -> {
            playerLoses = true
            printBoard()
            println("You stepped on a mine and failed!")
            exitProcess(0)
        }
        in '1'..'8' -> playerBoard[r][c] = answerBoard[r][c]
        '/' -> {
            val queue: Queue<Pair<Int, Int>> = LinkedList()
            queue.add(Pair(r, c))
            while (queue.isNotEmpty()) {
                val (y, x) = queue.poll()
                if (y < 0 || y >= height || x < 0 || x >= width) continue
                if (playerBoard[y][x] != '.' && playerBoard[y][x] != '*') continue
                else {
                    playerBoard[y][x] = answerBoard[y][x]
                    if (playerBoard[y][x] == '/') {
                        queue.add(Pair(y - 1, x - 1))
                        queue.add(Pair(y - 1, x))
                        queue.add(Pair(y - 1, x + 1))
                        queue.add(Pair(y, x - 1))
                        queue.add(Pair(y, x + 1))
                        queue.add(Pair(y + 1, x - 1))
                        queue.add(Pair(y + 1, x))
                        queue.add(Pair(y + 1, x + 1))
                    } else continue
                }
            }
        }
    }
    printBoard()
    checkWin()
}

fun checkWin() {
    minesFound = 0
    spacesRevealed = 0
    for (r in 0 until height) {
        for (c in 0 until width) {
            if (playerBoard[r][c] == '*' && answerBoard[r][c] == 'X') minesFound++
            if (playerBoard[r][c] == '/' || playerBoard[r][c] in '1'..'8') spacesRevealed++
        }
    }
}

fun main() {
        print("How many mines do you want on the field? ")
        mines = readln().toInt()
    printBoard()
    while ((minesFound != mines || minesFlagged != mines) && spacesRevealed != height * width - mines) {
        print("Set/unset mine marks or claim a cell as free: ")
        val input = readln().split(" ")
        val col = input[0].toInt() - 1
        val row = input[1].toInt() - 1
        when (input[2]) {
            "mine" -> inputMine(row, col)
            "free" -> inputFree(row, col)
        }
    }
    println("Congratulations! You found all the mines!")
}

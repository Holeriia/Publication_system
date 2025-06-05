package publicationtracker.util

import cats.effect.IO
import org.apache.poi.xwpf.usermodel.XWPFDocument

import java.io.FileOutputStream

object WordExample {

  def createWordFile(path: String): IO[Unit] = IO {
    val doc = new XWPFDocument()
    val para = doc.createParagraph()
    val run = para.createRun()
    run.setText("Привет, мир!")

    val out = new FileOutputStream(path)
    doc.write(out)
    out.close()
    doc.close()
  }
}
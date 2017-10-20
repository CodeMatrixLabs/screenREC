package software.kanunnikoff.screenrec.model

/**
 * Created by dmitry on 17/10/2017.
 */
data class Record(
        var id: Long,
        var title: String,
        var outputFormat: String,
        var outputFile: String,
        var audioEncoder: String,
        var audioEncodingBitRate: String,
        var audioSamplingRate: String,
        var audioChannels: String,
        var videoEncoder: String,
        var videoEncodingBitRate: String,
        var videoFrameRate: String,
        var videoSize: String,
        var isFavored: Int,
        var fileSize: String,
        var duration: String,
        var date: String,
        var thumbnail: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        return if (other !is Record) {
            false
        } else other.id == id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + outputFormat.hashCode()
        result = 31 * result + outputFile.hashCode()
        result = 31 * result + audioEncoder.hashCode()
        result = 31 * result + audioEncodingBitRate.hashCode()
        result = 31 * result + audioSamplingRate.hashCode()
        result = 31 * result + audioChannels.hashCode()
        result = 31 * result + videoEncoder.hashCode()
        result = 31 * result + videoEncodingBitRate.hashCode()
        result = 31 * result + videoFrameRate.hashCode()
        result = 31 * result + videoSize.hashCode()
        result = 31 * result + isFavored
        result = 31 * result + fileSize.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + thumbnail.hashCode()
        return result
    }
}
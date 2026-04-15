package groovy

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream

import java.util.zip.GZIPInputStream

class Deserialize {

    static void onGroovyRegister() {

        byte[] gzipData = Base64.decoder.decode(text)

        // 2. GZIP 解压
        ByteArrayInputStream bis = new ByteArrayInputStream(gzipData);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        BukkitObjectInputStream input = new BukkitObjectInputStream(gzip);

        // 3. 反序列化为 ItemStack
        ItemStack item = (ItemStack) input.readObject();
        input.close()

        println item
    }

    static def text = "{\n" +
            "  \"pageData\": {\n" +
            "    \"1\": {\n" +
            "      \"storeItems\": [\n" +
            "        {\n" +
            "          \"base64\": \"H4sIAAAAAAAAAI2RzWsTQRjG36ZJjB9grXgRFA9eFLrxYrGUHqS1spCkhSAKOU3SMWwzs7vOvhsSD9KgtkpUCsaqBTHQwnooeNPQgEL+AP8GKdl8gB7qxauzSdOEQsBhmJkdfvvM87zvzm8IWALOGyKtJO1MRkPFRo0pmqHcFcQ0qfizePvX/rgR8MFIBEY5MRFOR5ZJloQ9MBwl5nTOlBLXUwZX0oaRZlSRR27ocmOMplBRObeRJBmV8OU4FRph2kO6NG8IDgfDB74E+DM0byGcTXT1GdHT4YXkspSYTkAwS5hNrQfwCPw50xY9TPEw5QBb/3Hv45h1hfkAcqaU9SP4ZmZQ7nmTIgQJN2wd5TenSGxPK9ilLg4UQNOzVEdD5BUVKY8jSWUQjs2pN6MLsTmZ9Ez/WVVHmqZifK+8/bewdkPWSIVAx2hOwFifi9k8ScXqzpsLJ0s/iz13oU4a6KyjfTfeIYRw3PM40TV+akmzTEbyEzqRt+BnhqCD9kOe06jkEU7cicUXb82q8+oswrmaQ+qVYnPXaW09dbfXGuWK+/2xzDD5H92KaBYObddIAkKUUS4rddiTAUNTNed+zeH1ysqRWXMm69Vis/pyGICgSGZvY7fm0Hap2l4pNz6sNzbeXzoc7vPPrcKLemWzWfrkvi4gXO39QNxnqy1nSyZuv/vSTdx69dX99ra5+aQbHeHasIeHzX8tUd1zIwMAAA\\u003d\\u003d\",\n" +
            "          \"amount\": 8\n" +
            "        },\n" +
            "        {\n" +
            "          \"base64\": \"H4sIAAAAAAAAAI2Rz2sTQRTHX9Mkxh9grXgRFA9eFDrxYlVKT8XqQlqDISjkNEnHsM3Mznb2bUg8SAPaKlUpGKsWxEIL66HgTYsBD/kD/BukJE0CetCLV2eTpglCoMMwv/jM933fe9u/IOQoOCtVlqTdXM5E4qLJiSnJPUVtm6k/8Vs/f4/KUACGYjAsqI1wMjZP8zTqg9EZak8UbC1xNSMFyUqZ5Yzoo5CW3jhnGSSGEC7SNGcavphgyqTcfMjmpqUSsD8CEEhBMMeKDsLpVEefUysbvZOe1xITKQjnKXeZswCPIFiwXdXFiI+RfWz1+/0PI84lHgAo2Fo2iBCYnES9F22GEKZCuhbqu2BIXV8r3KHO9xXAtPLMQqmKxEAmEkgzOYQjiWQsfjt5V2d6qhfWsJBlmRrd3dj6W1q+rmtkQKhttKBgpMfNuiLN1NL263PHyz9Wuu7C7WygvQ733PiHCMJR3+NYx/iJOdOxOS2OWVS/QpBLxfrtR3ynM5pHOJacTcRvThnTxhTCmap3rbaz2Hr7uVWutBY39t6v7q290zmMH6JbMdPBge0aSkGEcSZ0pQ560mfoRtV7UPWEjv3frHrjtcpKo/JiEIBANLO79rXqsX7TFw5G/dmnZul5bWe9Uf5Yf1VCuNz9QOtPl5reZnPzSX1rufnyS/3bm8b6434VhCuDAg+a/wDnagP+IwMAAA\\u003d\\u003d\",\n" +
            "          \"amount\": 6\n" +
            "        },\n" +
            "        {\n" +
            "          \"base64\": \"H4sIAAAAAAAAAI2Rz2sTQRTHX9IkxB/Q0l5EUDzUg0gmCP0Blh4kWAlNYnERhRx0Nhm328xk1tm3IfEgnrRQRA/VgyAVKewlBKQeSuMpf4B/gCeRbHPRg/4FziZtEwiCwzDvMfPlO5/3XvMXxF0F56WyiOlVKjYSD21ObEnuKeo4TP1Zu/Xz97SMRyGSgwlBHYTJ3Aat0XQoTOeps1R3tMV8SQpiSWlxRnQqZFUHzlkJSVYID6nJmRbPGkzZlNtPWHlFKgFHKwrRIsQqrOEizBQH/pxWrfRtc0NbLBUhUaPcY+5jeAqxuuOpYxkJZeRI9vrr/Z0p9wqPAtQdbRtDiC4vo44NhyEkylRQSycxwZB6oVdioLo40gC7WmNVlKpBssiEgbRUQUhmC6sPjBuZVV3q5PBfY10qXM8vzsw9/HZHt8iAeJ+zrmBqqCp4wmTqefPNhTPb37cGcJF+KdA/J4YoYZJEOBUCpgbUZ8u263DaSFWpCNm5VGyUPRli5rUe4fTdgrF2M5NdyWYQznX8ueBVM9jeDfY3g08fDjdbQet9b2dPl7DwH9PK2S7+c1yRIiQZZ0J36mQmI0ypjv+o44vu/rOT3fEXuu2tXvvl+BPCtH798fZAE/cOPg4o9VA6vtm/XDx89zl40e5+2Q1ae9cv6eur1+YvI8yOW43vv6D5MHHjAgAA\",\n" +
            "          \"amount\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"base64\": \"H4sIAAAAAAAAAI2Tz0sUYRjHH/eHaD/IlC6B0cEOHRw7lEbiYd1ddUhXaVcSlpB315dt3Hd+9M47NtshEiIF0yQ0kBILiulg1DHcQzCXbh36C0Lc1oU61KVrz8yqW1ji8PI+78z75fM87/d9Zv07hE0Op3WekzJWPq8IyRIKkxRdus6JYVD+c6T/249mPRyAukEIqsQQcGJwkkyRDk/YMUSMbttAxKWsrko5Xc8xKuFS1TUMjNGskGRVtQTJMIritiTlCmHKHTrRp3MVdp4ABNIQytOCKaAlXeUzouU6hjOTiOhOQ/0UYRY1b8FdCNmGxXdlkieTdmSLn8bWmszzLABgG4gNCgj09AgIiYJBMahUEMtD1Fc3z/xxbkWboprQeUGSBVWTgmTzeNSYHBkaTsTGR+To1chY3M8P/hzcA9V7iwYBjR6+vZrq2IRiGowU2jWiepmZzjE0UC17k2gCT9noZeljJGfWOCjwvg4hRsCR0URyJB6V++SogFbXyZQW1kqzq5XHxcryout0pV3n4uazd7i6gfZ3HsL+QcUU//W/Lo3VMaqiB3sm7xYWFnAB87iOuvX+3t5wnU7XYVvFuXJxfv+ugHOuQzefbJzFUF56Xa2+9OjtVvHV9oen2y8eluZnyhvOAejKyv3Km6V/oU+hHYh2HfJ1eba8MHMFk2RlAW37xfsHutV1CLd6lYPaFey/e6AObzQm94/jlcVjta+Y62StS2VN0BzlzZvPX/6anrmMpssQ9vva5tBU0yUsNUP5g/Xl1qNLX+Z2m9mDtfgi/xeNcE4K3pXa059bVz6S1aCHC5lYqy8P3A55s4DjA3IsPh5PRAciiVQSe9p/j6RS1+Te0VQ8af8GD1M7GgUEAAA\\u003d\",\n" +
            "          \"amount\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"base64\": \"H4sIAAAAAAAAAE2QO0sDQRCAx0sujYUxdoJWNjabShBCEMUHB/GBKRRS7cXhuNzu7bo3G+5SCDZa2FhorYVlfou/Qay10MbWjUmI08zAfHzzGH6CnxlYViZioU2SmJilWLBYsTPDtUbzfXLw8VVTvgdzLShJrgkWWj3e5/URWD/kupFrp9joKskipSKBzJVSpS4JgV1igZSWeCjQwWttNDEX8QAv9pWRMAkPvA6UEywygqXO2C94GtWPw55TNDpQ6XNhMbuEKyjn2popxkYYm2D3r+dP1WxdeAC5dtoSgddsEpSp0EhQ4VLZlOxIUhm3V/9dHqd9TEmZggWEsk28mxD4O6d727vuwMXZtCAljNDU3p9ffq5vN91rAvD/9ssNVGfckZUhmpvh48r8w9vddKmtX7CCP7R1AQAA\",\n" +
            "          \"amount\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"base64\": \"H4sIAAAAAAAAAI2RS2sTURTHT9MkxAda665FcSGIYqf4aBFKhVrbGkjT0igKWchNeg3TzMvJmZC4kEZEU9rEqolFF1akEBcpBRUR0yjkA/gZpCQzDSiiG7feyXRMhAheLvf1/3Hu/5xT/AaumAo9shrhQlo0yiOnIS9wvMxdVYmiUPXn9MTXH92yywEdPugUiYKw3zdH4qTfBPsniTKUUFiIgbAschFZjgiUY0dRltgmCDSMnFcUNSQhgTL4aICqPBH4W3R2XFZF2BkOcATBGaXJGMLBoBVfIFKkfyo0x0IMBcEdJ4JGYzfhNjgTiqbaGGdi3A62/PnaalfsuOAASCgsrBPBMTyMbE8qFMFNRFmTkN1FikQzY7kt6nBLAXgpTiWU1STnRSoGkISjCPsuzoxMTPkD1y/MjI1cvsQSPtD83SshjVC1e+vF2q/U/XOsVF5wNfwmVOhqcn5NDFH1XjF/aE/uy6Jt8nwjKWisnU1T5sGDsMu02mf53zvLxxSBJPskwl7BKcgqbc3CYxqeZDzC7iv+wPTYqHfcO4rQWymQajmr59PG82J95a3+uqTfTdfXc/qbPMtk8D9a5+Nj+M/edQTBQwUqsrL9aZBty41wplK4USmI1ffzrbNSGKyWFo1Spq2KcIwBW08+sFV/l96eX6k9Th35e2xnNmoLdxBO2iTRNx4YhUd66mVtLXPaePrRyK3WHpa/by7pSymjtD7ARIQeG6fV8icj94oVpP4sd8oSe23xrCXWlrPW93p2AeFEW69t528rVw/fWgMAAA\\u003d\\u003d\",\n" +
            "          \"amount\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"base64\": \"H4sIAAAAAAAAAI2QXWsTQRSGT/NRogiKXgheiBeiCHZTsC1K6YWUViKpBoMoBJFpOoZtZrPr5mxIvJBErK32g4b6UaKg1uJ6UY3iRWJiFfYH+CMku5uCIvoLnO02sQgRh2Fn2Hnf57znrH0Hf1qFA7KaEMa1ZFJEQUORCaIsXFKJolD1V+TMt597Zb8HusLglYiCsDs8STIk6AiDY0QZzCoc0R+XJSEhywlGBX6V5BQ/GKNxFEKSpCEZZ5SLD0epKhIm3qATo7IqwdbygCcGviTNpRH2xVw+I6lE8Pz4JEcMxqA7Q5hG09fhJviyiqa2ZIIjE7Zki18uP92TPsY8AFmFY70InqEhBB/mFMoPiSLRHES3+3hwW99iKkNTKKs5IYRUiiKJJxH8kdORkQubVWHz623bfc4lgLDDgfa4BXZNiGmFkVxPikhOPSar9I8BIeCgx7geYefFc9HIyHBoNDSMsN/Q+/t67ZpuVd5Z1Xsbt/LNcsWcXudzHfiPuYbFNHYcbFcMApRRiTfXnl4rEo9/ytCvGbrUqOb/2oY+0KjP2vX5TgIEgWu+PqwZOm0nPtRebivm3bL1ZM2+/8pcKiCcaBmIOVW05lc28iXzE6/U54obH2ablTJ/tUu320SE4y1XnHMatQXXu21e1lzRmlk2Z6o/Phb+3ZFdL5pvSp07OmLoJ2M8j6Ez+2XBer3SqK1ay0v87xVD52B2VtTi4lWEozxN88WUuTrtIs3FBfPtHevRM+v9g+bnx83nc64TobdTtU77N7xlnOeUAwAA\",\n" +
            "          \"amount\": 1\n" +
            "        }\n" +
            "      ],\n" +
            "      \"unLockSlot\": 9\n" +
            "    }\n" +
            "  },\n" +
            "  \"autoPickup\": false\n" +
            "}"
}

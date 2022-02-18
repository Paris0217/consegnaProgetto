<!doctype html>
<html lang="it">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Calculate the distance between two points">
    <meta name="author" content="Francesco Natati">
    <title>Distance Calculator</title>

    <link rel="stylesheet" href="public/css/main.css" />
  </head>
  <body>
    <nav class="navbar">
      <div class="container">
        <h1 class="title">Distance Calculator</h1>
      </div>
    </nav>
    <div class="main">
      <div class="container">
          <h2>Utilizzo</h2>
          Inviare una richiesta GET all'api specificando il punto di origine e il punto di arrivo:
          <div class="code">
            <code>
              [GET] <?php echo BASE_URL ?>?origin=<span class="highlight">YOUR+ORIGIN+POINT</span>&destination=<span class="highlight">YOUR+DESTINATION+POINT</span>
            </code>
          </div>

          Alcuni esempi possono essere:
          <div class="code">
            <code>
              [GET] <?php echo BASE_URL ?>?origin=<span class="highlight">Porta+San+Donato+Bologna</span>&destination=<span class="highlight">Stazione+Centrale+Bologna</span><br />
              [GET] <?php echo BASE_URL ?>?origin=<span class="highlight">Colosseo+Roma</span>&destination=<span class="highlight">Arena+Verona</span>
            </code>
          </div>

          L'output della richiesta HTTP, se corretta, conterrà la risposta in formato JSON formattata nella seguente maniera:
          <div class="code">
            <code>
              {
                "statusCode": 200/500,
                "distance": <span class="highlight">ANSWER_AS_FLOAT_VALUE</span>
              }
            </code>
          </div>

          La distanza è espressa in chilometri.
      </div>
    </div>
  </body>
</html>
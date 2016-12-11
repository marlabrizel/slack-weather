# slack-weather

A basic slack integration for fetching the weather

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

You'll also need access to a Slack organization where you have permission to
create a webhook, as well as an API key for
[OpenWeatherMap](https://openweathermap.org/current)

## Running Locally

To start a web server for the application, run:

    lein ring server

## Running in Production
To deploy and run in your own Slack organization, first clone this repository:
    
    git clone git@github.com:marlabrizel/slack-weather.git

You'll then need to create a `profiles.clj` in your project root. You'll store
your own api key for OpenWeatherMap's [Current Weather
API](https://openweathermap.org/current) in here plus your Slack webhook and
authentication token. See below for a sample `profiles.clj`

```clojure
{:dev-overrides
 {:env
   {:api-key "OPEN_WEATHER_API_KEY"
    :hook-url "SLACK_HOOK_URL"
    :auth-token "SLACK_AUTH_TOKEN"}}}
```

Once you've set up your `profiles.clj`, you'll need to deploy the service
somewhere. I like Heroku because it's free for the basic tier, plus they offer
[well-documented](https://devcenter.heroku.com/articles/getting-started-with-clojure#deploy-the-app)
Clojure support. If you prefer something else, go nuts.

## License

Copyright © 2016 [Marla Brizel](www.github.com/marlabrizel)

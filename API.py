from flask import Flask, jsonify, request

from FYP_SEGMENTATION import recommend_products, model

app = Flask(__name__)

stored_data = {}  # Dictionary to store the sign-up data

# creating instances of segmentation and recommendation models
segmentation_model = model
recommendation_model =  recommend_products


@app.route('/')
def hello():
    return 'Hello, World!'


@app.route('/signin', methods=['GET', 'POST'])
def signup():
    data = request.get_json()

    # Extract data from the request
    username = data.get('username')
    education = data.get('education')
    marital_status = data.get('marital_status')  # Update the key here
    income = data.get('income')
    kidHome = data.get('kidHome')
    teenHome = data.get('teenHome')
    recent = data.get('recent')
    totalSpent = data.get('totalSpent')

    # Store the data in the dictionary
    stored_data[username] = {
        'education': education,
        'marital_status': marital_status,  # Update the key here
        'income': income,
        'kidHome': kidHome,
        'teenHome': teenHome,
        'recent': recent,
        'totalSpent': totalSpent,
    }

    data = [[education, marital_status, income, kidHome, teenHome, recent, totalSpent]]

    # call segmentation model to get results
    result = model.predict(data)

    # extract the single value from the result list
    recommend = f'Cluster {result[0]}'

    # call recommendation model to get results
    results = recommend_products(recommend)

    # Log the received data
    print('Received signup data:', stored_data)
    print(recommend)
    print('result', results)

    # call recommendation model to get results
    return jsonify(results)

    # Prepare the response
    response = {
        'Results': results
    }

    return jsonify(response)


if __name__ == '__main__':
    app.run()

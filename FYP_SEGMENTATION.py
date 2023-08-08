# -*- coding: utf-8 -*-
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt 
import seaborn as sns
from sklearn.cluster import KMeans
from sklearn import preprocessing
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import PCA
from sklearn.metrics.pairwise import cosine_similarity
import random
pd.set_option('display.max_rows', None)
pd.set_option('display.max_columns', None)
df = pd.read_csv(r"marketing_campaign.csv" , sep="\t")
productsdf = pd.read_csv(r"Products.csv")

# EDA
# Checking and Dropping Null Values 
df.isnull().sum()
df['Income'].fillna("0",inplace=True)
df.info()

# Changing datatype of Income from Object To Float
df['Income']=pd.to_numeric(df['Income'])
df.info()
df.describe()

#Feature Engineering and deleting unnecessary columns
df['Marital_Status'] = df['Marital_Status'].replace(['Married', 'Together'],'Relationship')

df['Marital_Status'] = df['Marital_Status'].replace(['Divorced', 'Widow', 'Alone', 'YOLO', 'Absurd'],
                                                    'Single')
df['Age'] = 2022-df['Year_Birth']

df['Education'] = df['Education'].replace(['PhD', 'Master'],'PostGrad')  

df['Education'] = df['Education'].replace(['Basic','2n Cycle','Graduation'], 'UnderGrad')

df['TotalSpent'] = df['MntWines'] +df['MntFruits'] + df['MntMeatProducts'] + df['MntFishProducts'] + df['MntSweetProducts']+ df['MntGoldProds']

df['TotalAcceptedCoupons'] = df['AcceptedCmp1'] + df['AcceptedCmp2'] + df['AcceptedCmp3'] + df['AcceptedCmp4'] + df['AcceptedCmp5'] + df['Response']

df['TotalPurchases'] = df['NumWebVisitsMonth'] + df['NumWebPurchases'] + df['NumCatalogPurchases'] + df['NumStorePurchases']
df['TotalKids'] =df['Kidhome'] + df['Teenhome']
df = df.drop(columns=["Year_Birth","Z_CostContact", "Z_Revenue","Dt_Customer","AcceptedCmp1" , 
                    "AcceptedCmp2", "AcceptedCmp3" , "AcceptedCmp4","AcceptedCmp5", "Response",
                    "NumWebVisitsMonth", "NumWebPurchases","NumCatalogPurchases","NumStorePurchases"],
           axis=1)

df.head()

df.shape

#Encoding categorical data
df['Education'] = df['Education'].replace(['UnderGrad', 'PostGrad'], [0, 1])
df['Marital_Status'] = df['Marital_Status'].replace(['Single', 'Relationship'], [0, 1])

"""# Analysing Data and Looking for outliers"""

# df.plot('Marital_Status','TotalSpent', kind='scatter')
# plt.title("Frequency of TotalSpent per values in Marital_Status\n",fontsize=20)
# plt.figure(figsize=(8,8))
# plt.show()

# df.plot('Age','Income', kind='scatter')
# plt.title("Income Per Age\n",fontsize=20)
# plt.show()

# df.plot('Marital_Status','Income', kind='scatter')
# plt.title("Income per Marital_Status\n",fontsize=20)
# plt.show()

# df.plot('Kidhome','TotalSpent', kind='scatter')
# plt.title("TotalSpent per Kidhome\n",fontsize=20)
# plt.show()

"""# Found Outliers in Income, Age and TotalSpent. Removing them."""

df = df[(df["Age"]<85)]
df = df[(df["Income"]<600000)]

df = df[(df["Marital_Status"] == 0 ) & (df['TotalSpent'] < 2100) | (df["Marital_Status"] ==  1) & (df['TotalSpent'] < 2350)]

#Checking unique values in Children variables 
# df['Kidhome'].unique()

# df['Teenhome'].unique()

# """# Exploring The Dataset"""

# sns.barplot(x="Kidhome", y="TotalPurchases", data=df)
# plt.title("Checking TotalPurchases in comparison on Number of kids\n", fontsize=20)
# plt.show()

# df['Marital_Status'].value_counts().plot(kind='bar')
# plt.title("Frequency Of Each Category in the Marital_Status Variable \n",fontsize=20)
# plt.xticks(rotation=360)

# df['Education'].value_counts().plot(kind='bar')
# plt.title("Frequency Of Each Category in the Marital_Status Variable \n",fontsize=20)
# plt.xticks(rotation=360)

# df['Kidhome'].value_counts().plot(kind='bar')
# plt.title("Frequency Of Each Category in the KidHome \n",fontsize=24)
# plt.xticks(rotation=360)

# df['Teenhome'].value_counts().plot(kind='bar')
# plt.title("Frequency Of Each Category in the Teenhome \n",fontsize=24)
# plt.xticks(rotation=360)

# df.plot('Teenhome','TotalSpent', kind='scatter')
# plt.title("TotalSpent with Teenhome comaprison \n",fontsize=20)
# plt.show()

# sns.barplot(x="Kidhome", y="TotalAcceptedCoupons", data=df)
# plt.title("OfferAccepted Per KidHome\n", fontsize=20)
# plt.show()

# plt.title("Age Distribution\n",fontsize=20)
# sns.distplot(df['Age'])

# df.plot('Age','TotalSpent', kind='scatter')
# plt.title("TotalSpent Per Age\n",fontsize=20)
# plt.show()

# df.plot('Age', 'TotalSpent', kind='scatter')
# plt.title("Spending over Age\n", fontsize=20)
# plt.show()

# plt.title("TotalSpending over Education\n",fontsize=24)
# sns.barplot(x="Education", y="TotalSpent", data=df)
# plt.figure(figsize=(8,8))

# plt.title("TotalSpending over Marital_Status\n",fontsize=24)
# sns.barplot(x="Marital_Status", y="TotalSpent", data=df)

# #Plotting a heatmap for checking correlations
# temp= df.corr()
# plt.figure(figsize=(20,20))
# sns.heatmap(temp,annot=True)

"""# Standardization The Data"""

#Standardization the data
standard = StandardScaler()
v = df.copy()
v = df.drop(['ID'],axis=1)
v.head()
standard.fit(v)

scaled = standard.transform(v)

df.columns

"""# Model Training"""

clusters6 = v[['Education','Marital_Status','Income', 'Kidhome', 'Teenhome', 'Recency', 'TotalSpent']]
inertias = []

for i in range(1,11):
    kmeanModel = KMeans(n_clusters=i)
    kmeanModel.fit(clusters6)
    inertias.append(kmeanModel.inertia_)

#Plotting Elbow Graph to find k value
# plt.plot(range(1,11), inertias, 'bx-')
# plt.title('Elbow method')
# plt.xlabel('Number of Clusters')
# plt.ylabel('Inertia')
# plt.show()

model = KMeans(n_clusters=3, init='k-means++', random_state=42)
model.fit(clusters6)
label = model.predict(clusters6)
clusters6['clusters'] = label

"""# Identifying The Characteristics Of Each Cluster"""

# For every columns in dataset
# copy=  df.copy()
# copy=copy.drop(columns=['ID'])
# copy['clusters']=label
# for i in copy:
#     g = sns.FacetGrid(copy, col = "clusters", hue = "clusters", palette = "coolwarm", sharey=False, sharex=False)
#     g.map(sns.histplot,i) 
    
#     g.set_xticklabels(rotation=30)
#     g.set_yticklabels()
#     g.fig.set_figheight(5)
#     g.fig.set_figwidth(20)

# sns.scatterplot('Income','TotalSpent',hue=clusters6['clusters'],data=v)

# sns.countplot(x=clusters6['clusters'])
# plt.title("Distribution Of The Clusters")
# plt.show()

"""# Findings

1 postgrad, both, 2nd highest income, most prolly no child,  a teen, 2nd most less people who hasnt brought for long, meat and wine, all age
2 some, undergrad both, lowest income, a child, no teen, most recent but also most no recent, fish and gold, lowes spend, youngest
3 postgrad, both, highest income, no child, no teen, 2nd most recent, meat and wine and gold, highes spender, avg age
4 postgrad, highest singles, average income, maybe a child, a teen,most less people who hasnt brought for long, meat and wine, 2nd lowest, kinda old

# Testing
"""

# z=[[1,1,80000,0,0,32,2500]]
# x=model.predict(z)
# x

# type(x)

# z=[[0,1,10000,1,0,42,100]]
# x=model.predict(z)
# x

# z=[[1,1,60000,1,0,42,500]]
# x=model.predict(z)
# x

"""# Recommendation"""

columns = ['Drinks','Meat','Sweets','Fish','Gold','Electronic','Fruits']
headers = [[5,4,3,3,3,3.5,2.5],[5,1,1,4,2,5,1],[5,5,5,5,5,2.5,3]]
index = ['Cluster 0','Cluster 1','Cluster 2']
clustersdf = pd.DataFrame(headers, columns=columns,index=index)
clustersdf['index_column'] = clustersdf.index

clustersdf = clustersdf.drop(['index_column'],axis=1)
clustersdf

clustersdf.T

"""# Recommendation Model"""

def recommend_products(cluster, recursion_level=0):
    # Check if we have exceeded the recursion level threshold
    if recursion_level > 3:
        return []
    
    # Get the cluster's category ratings
    cluster_ratings = clustersdf.loc[cluster]

    # Sort the ratings in descending order and get the top 3 categories
    top_categories = cluster_ratings.sort_values(ascending=False)[:3]

    # Create a list of recommended products based on the top categories
    recommended_products = []
    for category in top_categories.index:
        # Get the products that match the category
        matching_products = productsdf.loc[productsdf['Category'] == category, 'Product Name'].tolist()
        # Add the matching products to the recommended products list
        recommended_products += matching_products

    # Remove duplicates from the recommended products list
    recommended_products = list(set(recommended_products))

    # Find the most similar cluster to the given cluster
    cluster_vector = cluster_ratings.values.reshape(1, -1)
    similarities = cosine_similarity(cluster_vector, clustersdf.values)
    most_similar_cluster_index = similarities.argsort()[0][-2]
    most_similar_cluster = clustersdf.index[most_similar_cluster_index]

    # Get the recommended products for the most similar cluster, if it is not the same as the current cluster
    if most_similar_cluster != cluster:
        similar_products = recommend_products(most_similar_cluster, recursion_level=recursion_level+1)
        recommended_products += similar_products

    # Remove duplicates from the combined list
    recommended_products = list(set(recommended_products))

    # Return the list of recommended products
    return recommended_products

"""# Testing"""

cluster = 'Cluster 0'
recommended_products = recommend_products(cluster)
print(f'Recommended products for cluster {cluster}: {recommended_products}')

def content_based_filtering(products):
    # Get the categories of the input products
    input_product_categories = productsdf.loc[productsdf['Product Name'].isin(products), 'Category']
    # Filter products with matching categories
    similar_products = productsdf[productsdf['Category'].isin(input_product_categories)]['Product Name'].tolist()

    # Remove the input products from the list
    similar_products = [product for product in similar_products if product not in products]
    similar_products = similar_products[:9]
    random.shuffle(similar_products)

    # Return 9 similar products
    return similar_products























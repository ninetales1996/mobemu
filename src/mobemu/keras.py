import numpy as np
import pandas as pd
import matplot.pyplot as plt
import tensorflow as tf
from sklearn.metrics import classfication_report
from tensorflow.contrib.keras import models
from tensorflow.contrib.keras import layers,losses,optimizers,metrics,activations

columns = pd.read_csv('Community_file_all.csv')
feat_data = columns[]
feat_datadata.drop['world_label']
labels = columns['world_label']

from sklearn.model_selection import train_test_split

X_train, X_test, y_train, y_test = train_test_split(feat_data,labels,test_size=0.3,random_state=74)

dnn_model = models.Sequential()
dnn_keras_model.add(layer.Dense(units=128,input_dim=165,activation='sigmoid'))
dnn_keras_model.add(layer.Dense(units=128),activation='sigmoid' )
dnn_keras_model.add(layer.Dense(units=64),activation='relu' )
dnn_keras_model.add(layer.Dense(units=64),activation='relu' )
dnn_keras_model.add(layer.Dense(units=3),activation='softmax' )

dnn_keras_models.compile(loss='sparse_categorical_crossentropy',metrics=['accuracy'])
dnn_keras_model.fit(x_train,y_train,epchs=3)

dnn_keras_model.predict_classes(x_test)
print(classification_report(predictions,y_test))
package com.tritcorp.mt4s.rddTools

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SparkSession}

/* MT4S - Multiple Tests 4 Spark - a simple Junit/Scalatest testing framework for spark
* Copyright (C) 2018  Gauthier LYAN
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
object RddTools extends LazyLogging {

  private val conf = new SparkConf().setAppName("DataframeTools").setMaster("local[*]")
  private val ss: SparkSession = SparkSession.builder().config(conf).getOrCreate()
  private val sc: SparkContext = ss.sparkContext
  private val sqlContext: SQLContext = ss.sqlContext
  sc.setLogLevel("WARN")
  /**
    * Change it to convert csv rdd to dataframe.
    */
  var csvDelimiter:String =";"


  /**
    * Implicitly converts a RDD to a DebugRdd
    * @param rdd
    * @return
    */
  implicit def rdd2DebugRdd(rdd: RDD[Row]): DebugRDD = {
    new DebugRDD(rdd)
  }


  /**
    * Converts a RDD that contains csv info into a dataframe
    * - The first row of the rdd MUST be the csv header
    * - The rdd rows must all have the same length
    * @param rdd the rdd to convert
    * @return the dataframe from the rdd
    */
  implicit def rddCsvToDF(rdd: RDD[String]): DataFrame = {
    logger.warn("/!\\ DID YOU SET CORRECTLY THE csvDelimiter VALUE IN RddTools FOR RDD TO DF CONVERSION /!\\ ?")
    logger.warn("CURRENT DELIMITER : "+csvDelimiter)

    def dropFirst(rdd: RDD[Row]): RDD[Row] = {
      val fst = rdd.first
      rdd.filter(line => line != fst)
    }

    val res = rdd.map(line => line.split(csvDelimiter))
      .filter(line => line.length > 1)
      .map(line => Row.fromSeq(line))

    val schema = StructType(res.first.toSeq.map(el => StructField(el.asInstanceOf[String], StringType, nullable = true)))

    val df = sqlContext.createDataFrame(dropFirst(res), schema)

  df
  }

}